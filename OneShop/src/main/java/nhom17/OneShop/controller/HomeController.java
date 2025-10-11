package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.Brand;
import nhom17.OneShop.entity.Category;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.entity.Rating;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.repository.RatingRepository;
import nhom17.OneShop.service.CategoryService;
import nhom17.OneShop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String homePage(Model model) {
        List<Category> allCategories = categoryService.findAll();

        // LOGIC MỚI: Nhóm danh sách danh mục, mỗi nhóm 6 danh mục
        final int CATEGORY_CHUNK_SIZE = 6;
        final AtomicInteger categoryCounter = new AtomicInteger();
        Collection<List<Category>> groupedCategories = allCategories.stream()
                .collect(Collectors.groupingBy(it -> categoryCounter.getAndIncrement() / CATEGORY_CHUNK_SIZE))
                .values();
        model.addAttribute("groupedCategories", groupedCategories);
        model.addAttribute("categories", allCategories); // Vẫn gửi list phẳng cho các trang khác

        // Lấy 16 sản phẩm mới nhất
        Page<Product> productPage = productService.searchUserProducts(null, null, null, "newest", null, 1, 16);
        List<Product> allProducts = productPage.getContent();

        // Nhóm 16 sản phẩm thành các nhóm nhỏ, mỗi nhóm 8 sản phẩm
        final int PRODUCT_CHUNK_SIZE = 8;
        final AtomicInteger productCounter = new AtomicInteger();
        Collection<List<Product>> groupedProducts = allProducts.stream()
                .collect(Collectors.groupingBy(it -> productCounter.getAndIncrement() / PRODUCT_CHUNK_SIZE))
                .values();
        model.addAttribute("groupedProducts", groupedProducts);

        // Lấy sản phẩm cho slider chính
        List<Product> sliderProducts = allProducts.stream().limit(4).toList();
        model.addAttribute("sliderProducts", sliderProducts);

        return "user/index";
    }

    @GetMapping("/shop")
    public String shopPage(Model model,
                           @RequestParam(name = "categoryId", required = false) Integer categoryId,
                           @RequestParam(name = "minPrice", required = false) String minPriceStr,
                           @RequestParam(name = "maxPrice", required = false) String maxPriceStr,
                           @RequestParam(name = "brandIds", required = false) List<Integer> brandIds,
                           @RequestParam(name = "sort", required = false) String sort,
                           @RequestParam(name = "page", defaultValue = "1") int page,
                           @RequestParam(name = "size", defaultValue = "9") int size) {

        BigDecimal minPrice = null;
        if (minPriceStr != null && !minPriceStr.isEmpty()) {
            try {
                String cleanPrice = minPriceStr.replaceAll("[.,₫\\s]", "");
                minPrice = new BigDecimal(cleanPrice);
            } catch (NumberFormatException e) {
                System.err.println("Invalid minPrice format: " + minPriceStr);
            }
        }

        BigDecimal maxPrice = null;
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            try {
                String cleanPrice = maxPriceStr.replaceAll("[.,₫\\s]", "");
                maxPrice = new BigDecimal(cleanPrice);
            } catch (NumberFormatException e) {
                System.err.println("Invalid maxPrice format: " + maxPriceStr);
            }
        }

        Page<Product> productPage = productService.searchUserProducts(categoryId, minPrice, maxPrice, sort, brandIds, page, size);

        List<Category> categoryList = categoryService.findAll();
        List<Brand> brandList = brandRepository.findAll();

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryList);
        model.addAttribute("brands", brandList);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedBrandIds", brandIds);

        return "user/shop/shop-sidebar";
    }

    @GetMapping("/contact")
    public String contactPage() { return "user/general/contact"; }

    @GetMapping("/about-us")
    public String aboutUsPage() { return "user/general/about-us"; }

    @GetMapping("/privacy-policy")
    public String privacyPolicyPage() { return "user/general/privacy-policy"; }

    @GetMapping("/product/{id}")
    public String productDetailPage(@PathVariable("id") int productId, Model model, HttpSession session) {
        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/shop";
        }
        model.addAttribute("product", product);

        List<Rating> reviews = ratingRepository.findBySanPham_MaSanPhamOrderByNgayTaoDesc(productId);
        model.addAttribute("reviews", reviews);

        if (reviews != null && !reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(Rating::getDiemDanhGia)
                    .average()
                    .orElse(0.0);
            model.addAttribute("averageRating", averageRating);
            model.addAttribute("totalReviews", reviews.size());
        } else {
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("totalReviews", 0);
        }

        @SuppressWarnings("unchecked")
        List<Integer> viewedProductIds = (List<Integer>) session.getAttribute("viewedProductIds");
        if (viewedProductIds == null) {
            viewedProductIds = new LinkedList<>();
        }

        viewedProductIds.remove(Integer.valueOf(productId));
        viewedProductIds.add(0, productId);

        if (viewedProductIds.size() > 10) {
            viewedProductIds = viewedProductIds.subList(0, 10);
        }
        session.setAttribute("viewedProductIds", viewedProductIds);

        if (viewedProductIds.size() > 1) {
            List<Integer> idsToFetch = new ArrayList<>(viewedProductIds);
            idsToFetch.remove(Integer.valueOf(productId));
            if (!idsToFetch.isEmpty()) {
                List<Product> recentlyViewedProducts = productRepository.findAllById(idsToFetch);
                model.addAttribute("recentlyViewedProducts", recentlyViewedProducts);
            } else {
                model.addAttribute("recentlyViewedProducts", Collections.emptyList());
            }
        } else {
            model.addAttribute("recentlyViewedProducts", Collections.emptyList());
        }

        return "user/shop/single-product";
    }

    @GetMapping("/api/product/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductForQuickView(@PathVariable("id") int productId) {
        Product product = productService.findById(productId);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }
}