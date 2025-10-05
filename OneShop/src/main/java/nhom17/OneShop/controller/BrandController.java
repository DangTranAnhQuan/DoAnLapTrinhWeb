package nhom17.OneShop.controller;

import nhom17.OneShop.entity.ThuongHieu;
import nhom17.OneShop.request.BrandRequest;
import nhom17.OneShop.service.BrandService;
import nhom17.OneShop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public String listBrands(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) Boolean status,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int size,
                             Model model) {
        Page<ThuongHieu> brandPage = brandService.searchAndFilter(keyword, status, page, size);
        model.addAttribute("brandPage", brandPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("brand", new BrandRequest()); // Dùng cho form add/edit
        return "admin/products/brands";
    }

    @PostMapping("/save")
    public String saveBrand(@ModelAttribute BrandRequest brandRequest,
                            @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Boolean status,
                            RedirectAttributes redirectAttributes) {
        if (!hinhAnhFile.isEmpty()) {
            String fileName = storageService.storeFile(hinhAnhFile, "brands");
            brandRequest.setHinhAnh(fileName);
        }
        brandService.save(brandRequest);

        // Giữ lại tham số phân trang và filter khi redirect
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        if (keyword != null) redirectAttributes.addAttribute("keyword", keyword);
        if (status != null) redirectAttributes.addAttribute("status", status);
        return "redirect:/admin/brand";
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable int id,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Boolean status,
                              RedirectAttributes redirectAttributes) {
        brandService.delete(id);

        // Giữ lại tham số phân trang và filter khi redirect
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        if (keyword != null) redirectAttributes.addAttribute("keyword", keyword);
        if (status != null) redirectAttributes.addAttribute("status", status);
        return "redirect:/admin/brand";
    }
}
