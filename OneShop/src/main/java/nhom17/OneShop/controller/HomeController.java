package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Category;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.service.CategoryService;
import nhom17.OneShop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String homePage() {
        return "user/index";
    }

    @GetMapping("/cart")
    public String cartPage(Model model) {
        List<Product> cartItems = new ArrayList<>();
        Product product1 = productService.findById(1);
        Product product2 = productService.findById(2);
        
        if (product1 != null) cartItems.add(product1);
        if (product2 != null) cartItems.add(product2);

        model.addAttribute("cartItems", cartItems);
        return "user/shop/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage() {
        return "user/shop/checkout";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "user/general/contact";
    }

    @GetMapping("/about-us")
    public String aboutUsPage() {
        return "user/general/about-us";
    }

    @GetMapping("/shop")
    public String shopPage(Model model,
                           @RequestParam(name = "categoryId", required = false) Integer categoryId,
                           @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                           @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                           @RequestParam(name = "sort", required = false, defaultValue = "newest") String sort,
                           @RequestParam(name = "page", defaultValue = "1") int page, // Người dùng thấy từ trang 1
                           @RequestParam(name = "size", defaultValue = "9") int size) {

        // Gọi service với các tham số từ request
        Page<Product> productPage = productService.searchUserProducts(categoryId, minPrice, maxPrice, sort, page, size);

        // Lấy danh sách danh mục
        List<Category> categoryList = categoryService.findAll();

        // Đưa dữ liệu vào Model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryList);
        
        // Các thuộc tính cho phân trang và giữ trạng thái filter
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("currentPage", page); // Số trang hiện tại người dùng thấy
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "user/shop/shop-sidebar";
    }

    @GetMapping("/sign-in")
    public String signInPage() {
        return "user/account/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUpPage() {
        return "user/account/sign-up";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "user/account/forgot-password";
    }

    @GetMapping("/my-account")
    public String myAccountPage() {
        return "user/account/my-account";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicyPage() {
        return "user/general/privacy-policy";
    }

    @GetMapping("/product/{id}")
    public String productDetailPage(@PathVariable("id") int productId, Model model) {
        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/shop"; 
        }
        model.addAttribute("product", product);
        return "user/shop/single-product-4";
    }

    @GetMapping("/wishlist")
    public String wishlistPage(Model model) {
        List<Product> wishlistItems = new ArrayList<>();
        Product product1 = productService.findById(3);
        Product product2 = productService.findById(4);
        
        if (product1 != null) wishlistItems.add(product1);
        if (product2 != null) wishlistItems.add(product2);

        model.addAttribute("wishlistItems", wishlistItems);
        return "user/shop/wishlist";
    }
}