package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Rating;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.repository.CategoryRepository;
import nhom17.OneShop.repository.RatingRepository;
import nhom17.OneShop.request.ProductRequest;
import nhom17.OneShop.service.ProductService;
import nhom17.OneShop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private RatingRepository danhGiaRepository;

    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Boolean status,
                               @RequestParam(required = false) Integer categoryId,
                               @RequestParam(required = false) Integer brandId,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        // ✅ Truyền các tham số mới vào service
        Page<Product> productPage = productService.searchProducts(keyword, status, categoryId, brandId, sort, page, size);

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("sort", sort);
        return "admin/products/products";
    }

    @GetMapping("/{id}")
    public String viewProductDetail(@PathVariable("id") int id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/admin/product";
        }
        List<Rating> reviews = danhGiaRepository.findBySanPham_MaSanPhamOrderByNgayTaoDesc(id);
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        return "admin/products/productDetail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Boolean status,
                              @RequestParam(required = false) Integer categoryId,
                              @RequestParam(required = false) Integer brandId,
                              @RequestParam(required = false) String sort,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size) {
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("sort", sort);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "admin/products/addOrEditProduct";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Boolean status,
                               @RequestParam(required = false) Integer categoryId,
                               @RequestParam(required = false) Integer brandId,
                               @RequestParam(required = false) String sort,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/admin/product";
        }
        ProductRequest productRequest = new ProductRequest();
        productRequest.setMaSanPham(product.getMaSanPham());
        productRequest.setTenSanPham(product.getTenSanPham());
        productRequest.setMoTa(product.getMoTa());
        productRequest.setGiaBan(product.getGiaBan());
        productRequest.setGiaNiemYet(product.getGiaNiemYet());
        productRequest.setHanSuDung(product.getHanSuDung());
        productRequest.setHinhAnh(product.getHinhAnh());
        productRequest.setKichHoat(product.isKichHoat());
        productRequest.setMaDanhMuc(product.getDanhMuc().getMaDanhMuc());
        productRequest.setMaThuongHieu(product.getThuongHieu().getMaThuongHieu());

        model.addAttribute("product", productRequest);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("brandId", brandId);
        model.addAttribute("sort", sort);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "admin/products/addOrEditProduct";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") ProductRequest productRequest,
                              @RequestParam("hinhAnhFile") MultipartFile hinhAnhFile,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Boolean status,
                              @RequestParam(required = false) Integer categoryId,
                              @RequestParam(required = false) Integer brandId,
                              @RequestParam(required = false) String sort,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size) {
        try {
            if (!hinhAnhFile.isEmpty()) {
                String fileName = storageService.storeFile(hinhAnhFile, "products");
                productRequest.setHinhAnh(fileName);
            }
            productService.save(productRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("categoryId", categoryId);
        redirectAttributes.addAttribute("brandId", brandId);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        return "redirect:/admin/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Boolean status,
                                @RequestParam(required = false) Integer categoryId,
                                @RequestParam(required = false) Integer brandId,
                                @RequestParam(required = false) String sort,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("status", status);
        redirectAttributes.addAttribute("categoryId", categoryId);
        redirectAttributes.addAttribute("brandId", brandId);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("page", page);
        redirectAttributes.addAttribute("size", size);
        return "redirect:/admin/product";
    }
}
