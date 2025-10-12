package nhom17.OneShop.controller;

import nhom17.OneShop.entity.ShippingCarrier;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.request.ShippingCarrierRequest;
import nhom17.OneShop.service.ShippingCarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shipping-carrier")
public class ShippingCarrierController {

    @Autowired
    private ShippingCarrierService shippingCarrierService;

    @GetMapping
    public String listCarriers(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        Page<ShippingCarrier> carrierPage = shippingCarrierService.search(keyword, page, size);
        model.addAttribute("carrierPage", carrierPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("carrier", new ShippingCarrierRequest());
        return "admin/system/shipping-carriers";
    }

    @PostMapping("/save")
    public String saveCarrier(@ModelAttribute ShippingCarrierRequest request,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size) {
        try {
            shippingCarrierService.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu nhà vận chuyển thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        return "redirect:/admin/shipping-carrier";
    }

    @GetMapping("/delete/{id}")
    public String deleteCarrier(@PathVariable int id,
                                RedirectAttributes redirectAttributes,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size) {
        shippingCarrierService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa nhà vận chuyển thành công!");
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        return "redirect:/admin/shipping-carrier";
    }
}
