//package nhom17.OneShop.controller;
//
//import jakarta.servlet.http.HttpSession;
//import nhom17.OneShop.entity.DiaChi;
//import nhom17.OneShop.entity.GioHang;
//import nhom17.OneShop.entity.NguoiDung;
//import nhom17.OneShop.repository.DiaChiRepository;
//import nhom17.OneShop.repository.NguoiDungRepository;
//import nhom17.OneShop.service.CartService;
//import nhom17.OneShop.service.CheckoutService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//import java.util.Optional;
//
//@Controller
//public class CheckoutController {
//
//    @Autowired private CartService cartService;
//    @Autowired private CheckoutService checkoutService;
//    @Autowired private DiaChiRepository diaChiRepository;
//    @Autowired private NguoiDungRepository nguoiDungRepository;
//
//    @GetMapping("/checkout")
//    public String showCheckoutPage(Model model, HttpSession session) {
//        List<GioHang> cartItems = cartService.getCartItems();
//        if (cartItems.isEmpty()) {
//            return "redirect:/cart";
//        }
//
//        NguoiDung currentUser = getCurrentUser();
//        List<DiaChi> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
//
//        BigDecimal subtotal = cartItems.stream().map(GioHang::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        BigDecimal couponDiscount = (BigDecimal) session.getAttribute("cartDiscount");
//        if (couponDiscount == null) {
//            couponDiscount = BigDecimal.ZERO;
//        }
//
//        BigDecimal membershipDiscount = BigDecimal.ZERO;
//        Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmailWithMembership(currentUser.getEmail());
//        if (userOpt.isPresent()) {
//            NguoiDung userWithTier = userOpt.get();
//            BigDecimal discountPercent = userWithTier.getHangThanhVien().getPhanTramGiamGia();
//            if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
//                membershipDiscount = subtotal.multiply(discountPercent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
//                model.addAttribute("membershipTier", userWithTier.getHangThanhVien());
//            }
//        }
//
//        BigDecimal shippingFee = BigDecimal.ZERO; // Tạm thời miễn phí vận chuyển
//        BigDecimal total = subtotal.subtract(couponDiscount).subtract(membershipDiscount).add(shippingFee);
//        if (total.compareTo(BigDecimal.ZERO) < 0) {
//            total = BigDecimal.ZERO;
//        }
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("addresses", addresses);
//        model.addAttribute("subtotal", subtotal);
//        model.addAttribute("couponDiscount", couponDiscount);
//        model.addAttribute("membershipDiscount", membershipDiscount);
//        model.addAttribute("shippingFee", shippingFee);
//        model.addAttribute("total", total);
//
//        return "user/shop/checkout";
//    }
//
//    @PostMapping("/place-order")
//    public String placeOrder(@RequestParam("shipping_address") Integer diaChiId,
//                             @RequestParam("payment_method") String paymentMethod,
//                             RedirectAttributes redirectAttributes) {
//        try {
//            checkoutService.placeOrder(diaChiId, paymentMethod);
//            return "redirect:/order-success";
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi đặt hàng: " + e.getMessage());
//            return "redirect:/checkout";
//        }
//    }
//
//    @GetMapping("/order-success")
//    public String orderSuccess() {
//        return "user/shop/order-success";
//    }
//
//    private NguoiDung getCurrentUser() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = ((UserDetails) principal).getUsername();
//        return nguoiDungRepository.findByEmail(username).orElseThrow();
//    }
//
//    @GetMapping("/checkout/edit-address/{id}")
//    public String showEditAddressForm(@PathVariable("id") Integer addressId, Model model) {
//        // Lấy thông tin địa chỉ từ CSDL
//        DiaChi address = diaChiRepository.findById(addressId)
//                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn âtại."));
//
//        model.addAttribute("address", address);
//        return "user/shop/edit-address"; // Trả về một file HTML mới
//    }
//
//    // XỬ LÝ LƯU THÔNG TIN ĐỊA CHỈ ĐÃ SỬA
//    @PostMapping("/checkout/save-address")
//    public String saveAddress(@ModelAttribute DiaChi address,
//                              @RequestParam(value = "return", required = false) String returnUrl,
//                              RedirectAttributes ra) {
//        // gán user nếu cần, validate...
//        diaChiRepository.save(address);
//        ra.addFlashAttribute("success", "Cập nhật địa chỉ thành công!");
//
//        // Nếu form truyền 'return', ưu tiên quay lại trang đó
//        if (returnUrl != null && !returnUrl.isBlank()) {
//            // Optional: chống open-redirect, chỉ cho phép đường dẫn nội bộ
//            if (returnUrl.startsWith("/")) {
//                return "redirect:" + returnUrl;
//            }
//            // fallback nếu ai đó sửa thành URL ngoài
//            return "redirect:/my-account?tab=addresses";
//        }
//
//        // Flow cũ (giữ nguyên nếu không có return)
//        return "redirect:/checkout";
//    }
//}
package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.Address;
import nhom17.OneShop.entity.Cart;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.AddressRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private CheckoutService checkoutService;
    @Autowired private AddressRepository diaChiRepository;
    @Autowired private UserRepository nguoiDungRepository;

    // Trang checkout: hiển thị địa chỉ + tóm tắt đơn + lựa chọn phương thức thanh toán
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        List<Cart> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        User currentUser = getCurrentUser();
        List<Address> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());

        BigDecimal subtotal = cartItems.stream()
                .map(Cart::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal couponDiscount = (BigDecimal) session.getAttribute("cartDiscount");
        if (couponDiscount == null) couponDiscount = BigDecimal.ZERO;

        BigDecimal membershipDiscount = BigDecimal.ZERO;
        Optional<User> userOpt = nguoiDungRepository.findByEmailWithMembership(currentUser.getEmail());
        if (userOpt.isPresent() && userOpt.get().getHangThanhVien() != null) {
            BigDecimal percent = userOpt.get().getHangThanhVien().getPhanTramGiamGia();
            if (percent != null && percent.compareTo(BigDecimal.ZERO) > 0) {
                membershipDiscount = subtotal.multiply(
                        percent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                );
                model.addAttribute("membershipTier", userOpt.get().getHangThanhVien());
            }
        }

        BigDecimal shippingFee = BigDecimal.ZERO; // Tạm miễn phí vận chuyển
        BigDecimal total = subtotal.subtract(couponDiscount).subtract(membershipDiscount).add(shippingFee);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("addresses", addresses);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("couponDiscount", couponDiscount);
        model.addAttribute("membershipDiscount", membershipDiscount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("total", total);

        return "user/shop/checkout";
    }

    // Nhận form đặt hàng
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam("shipping_address") Integer diaChiId,
                             @RequestParam("payment_method") String paymentMethod,
                             RedirectAttributes redirectAttributes) {
        try {
            // Tách luồng theo phương thức
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                // Tạo đơn hàng COD ngay
                checkoutService.placeOrder(diaChiId, "COD");
                return "redirect:/order-success?method=COD";
            } else if ("MOMO".equalsIgnoreCase(paymentMethod)) {
                // Chuyển sang trang/flow MoMo (placeholder nếu chưa tích hợp)
                return "redirect:/checkout/momo";
            } else if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
                // Chuyển sang trang/flow VNPAY (placeholder nếu chưa tích hợp)
                return "redirect:/checkout/vnpay";
            } else {
                redirectAttributes.addFlashAttribute("error", "Phương thức thanh toán không hợp lệ.");
                return "redirect:/checkout";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    // Trang thành công
    @GetMapping("/order-success")
    public String orderSuccess(@RequestParam(value = "method", required = false) String method, Model model) {
        model.addAttribute("method", method);
        return "user/shop/order-success";
    }

    // Trang sửa địa chỉ trong flow checkout
    @GetMapping("/checkout/edit-address/{id}")
    public String showEditAddressForm(@PathVariable("id") Integer addressId, Model model) {
        Address address = diaChiRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại."));
        model.addAttribute("address", address);
        return "user/shop/edit-address";
    }

    // Lưu địa chỉ đã sửa (hỗ trợ 'return' để quay lại /my-account?tab=addresses)
    @PostMapping("/checkout/save-address")
    public String saveAddress(@ModelAttribute Address address,
                              @RequestParam(value = "return", required = false) String returnUrl,
                              RedirectAttributes ra) {
        diaChiRepository.save(address);
        ra.addFlashAttribute("success", "Cập nhật địa chỉ thành công!");

        if (returnUrl != null && !returnUrl.isBlank()) {
            if (returnUrl.startsWith("/")) {
                return "redirect:" + returnUrl;
            }
            return "redirect:/my-account?tab=addresses";
        }
        return "redirect:/checkout";
    }

    // ====== Placeholder cho thanh toán MoMo/VNPAY (tạo view đơn giản để tránh 404/500) ======
    @GetMapping("/checkout/momo")
    public String momoGuide() {
        return "user/shop/momo-guide"; // tạo file templates/user/shop/momo-guide.html
    }

    @GetMapping("/checkout/vnpay")
    public String vnpayGuide() {
        return "user/shop/vnpay-guide"; // tạo file templates/user/shop/vnpay-guide.html
    }
    // =========================================================================================

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}
