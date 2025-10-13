package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.OrderStatusHistory;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.OrderStatusHistoryRepository;
import nhom17.OneShop.repository.ShippingCarrierRepository;
import nhom17.OneShop.request.OrderUpdateRequest;
import nhom17.OneShop.request.ShippingRequest;
import nhom17.OneShop.service.OrderService;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.service.OrderService; // Thêm import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired private UserRepository nguoiDungRepository;
    @Autowired private OrderService orderService;

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Autowired
    private ShippingCarrierRepository shippingCarrierRepository;

    @GetMapping
    public String listOrders(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) String paymentMethod,
                             @RequestParam(required = false) String paymentStatus,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size, // Tăng size mặc định lên 10
                             Model model) {

        Page<Order> orderPage = orderService.findAll(keyword, status, paymentMethod, paymentStatus, page, size);
        model.addAttribute("orderPage", orderPage);

        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("carriers", shippingCarrierRepository.findAll());
        model.addAttribute("shippingRequest", new ShippingRequest());

        return "admin/orders/orders";
    }

    @GetMapping("/{id}")
    public String showOrderDetail(@PathVariable long id, Model model,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String status,
                                  @RequestParam(required = false) String paymentMethod,
                                  @RequestParam(required = false) String paymentStatus,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Order order = orderService.findById(id);
        if (order == null) {
            return "redirect:/admin/order";
        }

        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setTrangThai(order.getTrangThai());
        updateRequest.setPhuongThucThanhToan(order.getPhuongThucThanhToan());
        updateRequest.setTrangThaiThanhToan(order.getTrangThaiThanhToan());

        model.addAttribute("order", order);
        model.addAttribute("orderUpdateRequest", updateRequest);

        // Truyền lại các tham số state để các liên kết sử dụng
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "admin/orders/orderDetail";
    }

    @GetMapping("/{id}/history")
    public String viewOrderHistory(@PathVariable long id, Model model,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String paymentMethod,
                                   @RequestParam(required = false) String paymentStatus,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        Order order = orderService.findById(id);
        if (order == null) {
            return "redirect:/admin/order";
        }

        List<OrderStatusHistory> historyList = historyRepository.findByDonHang_MaDonHangOrderByThoiDiemThayDoiDesc(id);

        model.addAttribute("order", order);
        model.addAttribute("historyList", historyList);

        // Truyền lại các tham số state để các liên kết sử dụng
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("paymentMethod", paymentMethod);
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("page", page);
        model.addAttribute("size", size);

        return "admin/orders/orderHistory";
    }

    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable long id,
                              @ModelAttribute("orderUpdateRequest") OrderUpdateRequest request,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String paymentMethod,
                              @RequestParam(required = false) String paymentStatus,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int size) {
        try {
            User adminUser = new User();
            adminUser.setMaNguoiDung(1); // Tạm thời hardcode

            orderService.update(id, request, adminUser);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật đơn hàng thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Luôn đính kèm các tham số state vào URL khi redirect
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("paymentMethod", paymentMethod);
        redirectAttributes.addAttribute("paymentStatus", paymentStatus);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);

        // Chuyển hướng về trang chi tiết
        return "redirect:/admin/order/" + id;
    }

//    User
@GetMapping("/my-orders")
public String myOrders(Model model) {
    User currentUser = getCurrentUser();
    model.addAttribute("user", currentUser);

    model.addAttribute("orders", orderService.findOrdersForCurrentUser());

    return "user/account/my-orders";
}

    @GetMapping("/order-details/{id}")
    public String orderDetails(@PathVariable("id") Long id, Model model) {
        try {
            Order order = orderService.findOrderByIdForCurrentUser(id);
            model.addAttribute("order", order);
            return "user/account/order-details";
        } catch (Exception e) {
            return "redirect:/my-orders";
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}