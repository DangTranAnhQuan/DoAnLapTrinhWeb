package nhom17.OneShop.controller;

import nhom17.OneShop.entity.DonHang;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.service.OrderService; // Thêm import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class OrderController {

    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private OrderService orderService;

    @GetMapping("/my-orders")
    public String myOrders(Model model) {
        NguoiDung currentUser = getCurrentUser();
        model.addAttribute("user", currentUser);

        model.addAttribute("orders", orderService.findOrdersForCurrentUser());

        return "user/account/my-orders";
    }

    @GetMapping("/order-details/{id}")
    public String orderDetails(@PathVariable("id") Long id, Model model) {
        try {
            DonHang order = orderService.findOrderByIdForCurrentUser(id);
            model.addAttribute("order", order);
            return "user/account/order-details";
        } catch (Exception e) {
            return "redirect:/my-orders";
        }
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}