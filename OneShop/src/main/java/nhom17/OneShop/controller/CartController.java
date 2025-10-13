package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.Cart;
import nhom17.OneShop.entity.Voucher;
import nhom17.OneShop.repository.VoucherRepository;
import nhom17.OneShop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private VoucherRepository khuyenMaiRepository;

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        List<Cart> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        BigDecimal subtotal = cartItems.stream()
                .map(Cart::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = (BigDecimal) session.getAttribute("cartDiscount");
        if (discount == null) {
            discount = BigDecimal.ZERO;
        }

        BigDecimal total = subtotal.subtract(discount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);
        model.addAttribute("appliedCouponCode", session.getAttribute("appliedCouponCode"));

        return "user/shop/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Integer productId,
                            @RequestParam(name="quantity", defaultValue="1") int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/update")
    public String updateCartItem(@RequestParam("productId") Integer productId,
                                 @RequestParam("quantity") int quantity) {
        cartService.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("productId") Integer productId) {
        cartService.removeItem(productId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/apply-coupon")
    public String applyCoupon(@RequestParam("coupon_code") String couponCode, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Voucher> couponOpt = khuyenMaiRepository.findByMaKhuyenMaiAndTrangThai(couponCode, 1); // 1 = Active

        if (couponOpt.isPresent() && couponOpt.get().getKetThucLuc().isAfter(LocalDateTime.now())) {
            Voucher coupon = couponOpt.get();
            session.setAttribute("cartDiscount", coupon.getGiaTri());
            session.setAttribute("appliedCouponCode", coupon.getMaKhuyenMai());
            redirectAttributes.addFlashAttribute("success", "Áp dụng mã giảm giá thành công!");
        } else {
            session.removeAttribute("cartDiscount");
            session.removeAttribute("appliedCouponCode");
            redirectAttributes.addFlashAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn.");
        }
        return "redirect:/cart";
    }
}