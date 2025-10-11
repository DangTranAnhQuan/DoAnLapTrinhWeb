package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Supplier;
import nhom17.OneShop.request.SupplierRequest;
import nhom17.OneShop.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/supplier")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public String listSuppliers(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size,
                                Model model) {
        Page<Supplier> supplierPage = supplierService.search(keyword, page, size);
        model.addAttribute("supplierPage", supplierPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("supplier", new SupplierRequest()); // Dùng cho modal
        return "admin/warehouse/suppliers";
    }

    @PostMapping("/save")
    public String saveSupplier(@ModelAttribute SupplierRequest supplierRequest,
                               RedirectAttributes redirectAttributes,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size) {
        supplierService.save(supplierRequest);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu nhà cung cấp thành công!");
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        return "redirect:/admin/supplier";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable int id,
                                 RedirectAttributes redirectAttributes,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "5") int size) {
        supplierService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa nhà cung cấp thành công!");
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        return "redirect:/admin/supplier";
    }
}
