package nhom17.OneShop.controller;

import nhom17.OneShop.entity.DonHang;
import nhom17.OneShop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/my-orders")
    public String showOrderHistory(Model model) {
        List<DonHang> orders = orderService.findOrdersForCurrentUser();
        model.addAttribute("orders", orders);
        return "user/account/my-orders";
    }

    @GetMapping("/order-details/{orderId}")
    public String showOrderDetail(@PathVariable("orderId") Long orderId, Model model) {
        try {
            DonHang order = orderService.findOrderByIdForCurrentUser(orderId);
            model.addAttribute("order", order);
            return "user/account/order-details";
        } catch (Exception e) {
            return "redirect:/my-orders"; // Hoặc trang báo lỗi
        }
    }
}