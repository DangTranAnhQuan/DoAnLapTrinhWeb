package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Category;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.request.CategoryRequest;
import nhom17.OneShop.service.CategoryService;
import nhom17.OneShop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StorageService storageService; // Tiêm StorageService

    @GetMapping
    public String listCategories(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Boolean status,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 Model model) {
        Page<Category> categoryPage = categoryService.searchAndFilter(keyword, status, page, size);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("category", new CategoryRequest());
        return "admin/products/categories";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute CategoryRequest categoryRequest,
                               @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Boolean status,
                               RedirectAttributes redirectAttributes){
        try {
            if (!hinhAnhFile.isEmpty()) {
                String fileName = storageService.storeFile(hinhAnhFile, "categories");
                categoryRequest.setHinhAnh(fileName);
            }
            categoryService.save(categoryRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu danh mục thành công!");
        } catch (DuplicateRecordException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi không mong muốn xảy ra!");
        }

        // Giữ lại tham số phân trang và filter khi redirect
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        if (keyword != null) redirectAttributes.addAttribute("keyword", keyword);
        if (status != null) redirectAttributes.addAttribute("status", status);
        return "redirect:/admin/category";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable int id,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) Boolean status,
                                 RedirectAttributes redirectAttributes){
        categoryService.delete(id);

        // Giữ lại tham số phân trang và filter khi redirect
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        if (keyword != null) redirectAttributes.addAttribute("keyword", keyword);
        if (status != null) redirectAttributes.addAttribute("status", status);
        return "redirect:/admin/category";
    }
}
