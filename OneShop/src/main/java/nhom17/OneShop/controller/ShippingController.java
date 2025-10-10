package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Shipping;
import nhom17.OneShop.repository.ShippingCarrierRepository;
import nhom17.OneShop.request.ShippingRequest;
import nhom17.OneShop.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shipping")
public class ShippingController {

    @Autowired
    private ShippingService shippingService;
    @Autowired private ShippingCarrierRepository shippingCarrierRepository;

    @GetMapping
    public String listShippings(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer carrierId,
                                @RequestParam(required = false) String status,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size, // Tăng size mặc định
                                Model model) {
        Page<Shipping> shippingPage = shippingService.search(keyword, carrierId, status, page, size);

        model.addAttribute("shippingPage", shippingPage);
        model.addAttribute("carriers", shippingCarrierRepository.findAll());
        model.addAttribute("shippingRequest", new ShippingRequest()); // Cho modal

        // Giữ lại trạng thái bộ lọc
        model.addAttribute("keyword", keyword);
        model.addAttribute("carrierId", carrierId);
        model.addAttribute("status", status);

        return "admin/orders/shippings"; // Thư mục shipping
    }

    @PostMapping("/save")
    public String saveShipping(@ModelAttribute ShippingRequest request,
                               RedirectAttributes redirectAttributes,
                               // Nhận các tham số state từ hidden input trong form
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer carrierId,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        try {
            shippingService.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin vận chuyển thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        // ✅ SỬA LỖI: Truyền giá trị của biến, không phải chuỗi ký tự
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("carrierId", carrierId);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);

        return "redirect:/admin/shipping";
    }

    @PostMapping("/saveFromOrder")
    public String saveFromOrder(@ModelAttribute ShippingRequest request,
                                RedirectAttributes redirectAttributes,
                                // Nhận các tham số state của trang Orders
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) String paymentMethod,
                                @RequestParam(required = false) String paymentStatus,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size) {
        try {
            shippingService.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã tạo đơn vận chuyển cho đơn hàng #" + request.getMaDonHang());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        // Redirect về trang Orders với đầy đủ state
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("paymentMethod", paymentMethod);
        redirectAttributes.addAttribute("paymentStatus", paymentStatus);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);

        return "redirect:/admin/order";
    }

    @GetMapping("/delete/{id}")
    public String deleteShipping(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Integer carrierId,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        try {
            shippingService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thông tin vận chuyển!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }

        // ✅ SỬA LỖI: Truyền giá trị của biến, không phải chuỗi ký tự
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("carrierId", carrierId);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);

        return "redirect:/admin/shipping";
    }
}
