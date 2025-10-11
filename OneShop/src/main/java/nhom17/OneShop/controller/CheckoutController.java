package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.DiaChi;
import nhom17.OneShop.entity.GioHang;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.repository.DiaChiRepository;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired private CartService cartService;
    @Autowired private CheckoutService checkoutService;
    @Autowired private DiaChiRepository diaChiRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session) {
        List<GioHang> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        NguoiDung currentUser = getCurrentUser();
        List<DiaChi> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());

        BigDecimal subtotal = cartItems.stream().map(GioHang::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal couponDiscount = (BigDecimal) session.getAttribute("cartDiscount");
        if (couponDiscount == null) {
            couponDiscount = BigDecimal.ZERO;
        }

        BigDecimal membershipDiscount = BigDecimal.ZERO;
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmailWithMembership(currentUser.getEmail());
        if (userOpt.isPresent()) {
            NguoiDung userWithTier = userOpt.get();
            BigDecimal discountPercent = userWithTier.getHangThanhVien().getPhanTramGiamGia();
            if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
                membershipDiscount = subtotal.multiply(discountPercent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                model.addAttribute("membershipTier", userWithTier.getHangThanhVien());
            }
        }

        BigDecimal shippingFee = BigDecimal.ZERO; // Tạm thời miễn phí vận chuyển
        BigDecimal total = subtotal.subtract(couponDiscount).subtract(membershipDiscount).add(shippingFee);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("addresses", addresses);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("couponDiscount", couponDiscount);
        model.addAttribute("membershipDiscount", membershipDiscount);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("total", total);

        return "user/shop/checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam("shipping_address") Integer diaChiId,
                             @RequestParam("payment_method") String paymentMethod,
                             RedirectAttributes redirectAttributes) {
        try {
            checkoutService.placeOrder(diaChiId, paymentMethod);
            return "redirect:/order-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi đặt hàng: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-success")
    public String orderSuccess() {
        return "user/shop/order-success";
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }

    @GetMapping("/checkout/edit-address/{id}")
    public String showEditAddressForm(@PathVariable("id") Integer addressId, Model model) {
        // Lấy thông tin địa chỉ từ CSDL
        DiaChi address = diaChiRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn âtại."));

        model.addAttribute("address", address);
        return "user/shop/edit-address"; // Trả về một file HTML mới
    }

    // XỬ LÝ LƯU THÔNG TIN ĐỊA CHỈ ĐÃ SỬA
    @PostMapping("/checkout/save-address")
    public String saveAddress(@ModelAttribute DiaChi address, RedirectAttributes redirectAttributes) {
        diaChiRepository.save(address);
        redirectAttributes.addFlashAttribute("success", "Cập nhật địa chỉ thành công!");
        return "redirect:/checkout";
    }
}