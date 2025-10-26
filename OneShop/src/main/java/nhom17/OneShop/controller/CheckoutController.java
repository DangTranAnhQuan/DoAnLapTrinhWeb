package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.dto.ShippingOptionDTO;
import nhom17.OneShop.entity.Address;
import nhom17.OneShop.entity.Cart;
import jakarta.servlet.http.HttpServletRequest;
import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.AddressRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.CheckoutService;
import nhom17.OneShop.service.ShippingFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Import if missing
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private CheckoutService checkoutService;
    @Autowired private AddressRepository diaChiRepository;
    @Autowired private UserRepository nguoiDungRepository;
    @Autowired private ShippingFeeService shippingFeeService; // Ensure this is Autowired

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        List<Cart> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        User currentUser = getCurrentUser(); // Assume user is logged in for checkout
        List<Address> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());

        BigDecimal subtotal = cartService.getSubtotal(); // Use service method

        // --- Discount Logic (Membership first, then Coupon) ---
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        Optional<User> userOpt = nguoiDungRepository.findByEmailWithMembership(currentUser.getEmail());
        if (userOpt.isPresent() && userOpt.get().getHangThanhVien() != null) {
            BigDecimal percent = userOpt.get().getHangThanhVien().getPhanTramGiamGia();
            if (percent != null && percent.compareTo(BigDecimal.ZERO) > 0) {
                membershipDiscount = subtotal.multiply(percent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                model.addAttribute("membershipTier", userOpt.get().getHangThanhVien());
            }
        }
        BigDecimal priceAfterMembershipDiscount = subtotal.subtract(membershipDiscount).max(BigDecimal.ZERO);
        BigDecimal couponDiscountValue = (BigDecimal) session.getAttribute("cartDiscount");
        couponDiscountValue = (couponDiscountValue == null) ? BigDecimal.ZERO : couponDiscountValue;
        BigDecimal actualCouponDiscount = couponDiscountValue.min(priceAfterMembershipDiscount);
        // --- End Discount Logic ---

        BigDecimal shippingFee = BigDecimal.ZERO; // Initial shipping fee is 0, JS will update
        BigDecimal total = priceAfterMembershipDiscount.subtract(actualCouponDiscount).add(shippingFee).max(BigDecimal.ZERO);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("addresses", addresses);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("couponDiscount", actualCouponDiscount); // Send actual applied coupon value
        model.addAttribute("membershipDiscount", membershipDiscount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("total", total);

        return "user/shop/checkout";
    }

    // ===== START: MODIFIED API - works with DTO =====
//    @GetMapping("/api/shipping-options")
//    @ResponseBody
//    public ResponseEntity<?> getShippingOptions(@RequestParam("province") String province) {
//        try {
//            BigDecimal subtotal = cartService.getSubtotal();
//            // Service now returns Optional<ShippingOptionDTO>
//            Optional<ShippingOptionDTO> cheapestOptionDto = shippingFeeService.findCheapestShippingOption(province, subtotal);
//
//            if (cheapestOptionDto.isPresent()) {
//                // Return the DTO
//                return ResponseEntity.ok(cheapestOptionDto.get());
//            } else {
//                Map<String, String> errorResponse = new HashMap<>();
//                errorResponse.put("error", "Không tìm thấy phương thức vận chuyển phù hợp cho tỉnh/thành này.");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//            }
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", "Lỗi khi tính phí vận chuyển: " + e.getMessage());
//            e.printStackTrace(); // Print stack trace for debugging
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
    // ===== END: MODIFIED API =====
    @GetMapping("/api/available-shipping-options") // <<< SỬA URL ENDPOINT
    @ResponseBody
    public ResponseEntity<?> getAvailableShippingOptions(@RequestParam("province") String province) { // <<< SỬA TÊN PHƯƠNG THỨC
        try {
            // Gọi service mới để lấy danh sách các lựa chọn (đã bao gồm freeship/giảm giá)
            List<ShippingOptionDTO> options = shippingFeeService.findAvailableShippingOptions(province);

            if (options != null && !options.isEmpty()) {
                // Trả về danh sách DTO
                return ResponseEntity.ok(options);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Không tìm thấy phương thức vận chuyển phù hợp cho tỉnh/thành này.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lấy phương thức vận chuyển: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // ===== START: MODIFIED placeOrder - accepts shipping details =====
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam("shipping_address") Integer diaChiId,
                             @RequestParam("payment_method") String paymentMethod,
                             @RequestParam("calculated_shipping_fee") BigDecimal shippingFee,
                             @RequestParam("shipping_method_name") String shippingMethodName,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) { // Bỏ HttpServletRequest nếu MOMO ko cần
        try {
            if (shippingMethodName == null || shippingMethodName.isBlank()) {
                throw new IllegalArgumentException("Chưa chọn được phương thức vận chuyển hợp lệ. Vui lòng chọn lại địa chỉ.");
            }
            if (shippingFee == null || shippingFee.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Phí vận chuyển không hợp lệ.");
            }

            // 1. TẠO ĐƠN HÀNG TRƯỚC
            Order order = checkoutService.placeOrder(diaChiId, paymentMethod, shippingFee, shippingMethodName);

            // 2. PHÂN LUỒNG THANH TOÁN
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                // Đơn hàng đã được tạo, chỉ cần chuyển đến trang thành công
                return "redirect:/order-success?method=COD";

            } else if ("ONLINE".equalsIgnoreCase(paymentMethod)) {

                return "redirect:/thanh-toan/qr?orderId=" + order.getMaDonHang();

            } else {
                redirectAttributes.addFlashAttribute("error", "Phương thức thanh toán không hợp lệ.");
                return "redirect:/checkout";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi đặt hàng: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/checkout";
        }
    }
    // ===== END: MODIFIED placeOrder =====

    // --- Other methods (Keep existing) ---
    @GetMapping("/order-success")
    public String orderSuccess(@RequestParam(value = "method", required = false) String method, Model model) {
        model.addAttribute("method", method);
        return "user/shop/order-success";
    }

    @GetMapping("/checkout/edit-address/{id}")
    public String showEditAddressForm(@PathVariable("id") Integer addressId, Model model) {
        Address address = diaChiRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại."));
        model.addAttribute("address", address);
        return "user/shop/edit-address";
    }

    @PostMapping("/checkout/save-address")
    public String saveAddress(@ModelAttribute Address address,
                              @RequestParam(value = "return", required = false) String returnUrl,
                              RedirectAttributes ra) {
        // **IMPORTANT: Set the current user to the address before saving**
        try {
            User currentUser = getCurrentUser();
            address.setNguoiDung(currentUser);
            diaChiRepository.save(address);
            ra.addFlashAttribute("success", "Cập nhật địa chỉ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi cập nhật địa chỉ: " + e.getMessage());
            // Decide where to redirect on error, maybe back to edit form?
            // return "redirect:/checkout/edit-address/" + address.getMaDiaChi();
        }


        if (returnUrl != null && !returnUrl.isBlank()) {
            if (returnUrl.startsWith("/")) {
                return "redirect:" + returnUrl;
            }
            return "redirect:/my-account?tab=addresses"; // Fallback
        }
        return "redirect:/checkout"; // Default redirect
    }

    @GetMapping("/checkout/momo")
    public String momoGuide() { return "user/shop/momo-guide"; }

    @GetMapping("/checkout/vnpay")
    public String vnpayGuide() { return "user/shop/vnpay-guide"; }

    // Helper method to get current user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Người dùng chưa đăng nhập."); // Throw exception if not logged in
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString(); // Fallback
        }
        return nguoiDungRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy người dùng hiện tại trong CSDL."));
    }
}