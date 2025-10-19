package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.*;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.repository.RatingRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.service.CategoryService;
import nhom17.OneShop.service.OrderService;
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

import java.security.Principal;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private BrandRepository brandRepository;
    @Autowired private RatingRepository ratingRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderService orderService;
    @Autowired private UserRepository userRepository;

    private Collection<List<Product>> groupProducts(List<Product> products, int chunkSize) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList();
        }
        final AtomicInteger counter = new AtomicInteger();
        return products.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize))
                .values();
    }

    @GetMapping("/")
    public String homePage(Model model) {
        // --- Phần code cũ (giữ nguyên) ---
        model.addAttribute("categories", categoryService.findAll());
        List<Product> sliderProducts = productService.findNewestProducts(4);
        model.addAttribute("sliderProducts", sliderProducts);

        int productLimit = 16; // Lấy 16 sản phẩm để có thể chia thành 2 slide
        int chunkSize = 8;     // Mỗi slide hiển thị 8 sản phẩm

        // Lấy và chia nhóm cho "Khám phá sản phẩm"
        List<Product> allProducts = productService.searchUserProducts(null, null, null, "newest", null, 1, productLimit).getContent();
        model.addAttribute("groupedProducts", groupProducts(allProducts, chunkSize));

        // Lấy và chia nhóm cho 3 mục mới
        model.addAttribute("newestProductsGrouped", groupProducts(productService.findNewestProducts(productLimit), chunkSize));
        model.addAttribute("topSellingProductsGrouped", groupProducts(productService.findTopSellingProducts(productLimit), chunkSize));
        model.addAttribute("mostDiscountedProductsGrouped", groupProducts(productService.findMostDiscountedProducts(productLimit), chunkSize));
        // =======================================================================

        return "user/index";
    }

    @GetMapping("/shop")
    public String shopPage(Model model,
                           @RequestParam(name = "keyword", required = false) String keyword,
                           @RequestParam(name = "categoryId", required = false) Integer categoryId,
                           @RequestParam(name = "minPrice", required = false) String minPriceStr,
                           @RequestParam(name = "maxPrice", required = false) String maxPriceStr,
                           @RequestParam(name = "brandIds", required = false) List<Integer> brandIds,
                           @RequestParam(name = "sort", required = false) String sort,
                           @RequestParam(name = "page", defaultValue = "1") int page,
                           @RequestParam(name = "size", defaultValue = "9") int size) {
        Page<Product> productPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchProductsForUser(keyword, page, size);
            model.addAttribute("searchResult", "Kết quả tìm kiếm cho: '" + keyword + "'");
        } else {
            BigDecimal minPrice = null;
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                try { minPrice = new BigDecimal(minPriceStr.replaceAll("[.,₫\\s]", "")); } catch (NumberFormatException e) { }
            }
            BigDecimal maxPrice = null;
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                try { maxPrice = new BigDecimal(maxPriceStr.replaceAll("[.,₫\\s]", "")); } catch (NumberFormatException e) { }
            }
            productPage = productService.searchUserProducts(categoryId, minPrice, maxPrice, sort, brandIds, page, size);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
        }
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrandIds", brandIds);
        model.addAttribute("keyword", keyword);
        return "user/shop/shop-sidebar";
    }

    @GetMapping("/product/{id}")
    public String productDetailPage(@PathVariable("id") int productId, Model model, HttpSession session, Principal principal) {
        Product product = productService.findById(productId);
        if (product == null) {
            return "redirect:/shop";
        }
        model.addAttribute("product", product);

        List<Rating> allReviews = ratingRepository.findBySanPham_MaSanPhamOrderByNgayTaoDesc(productId);

        boolean canReview = false;
        Rating currentUserReview = null;

        if (principal != null) {
            Optional<User> userOpt = userRepository.findByEmail(principal.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();
                Integer currentUserId = currentUser.getMaNguoiDung();

                Optional<Rating> reviewOpt = ratingRepository.findByNguoiDung_MaNguoiDungAndSanPham_MaSanPham(currentUserId, productId);
                if (reviewOpt.isPresent()) {
                    currentUserReview = reviewOpt.get();
                    allReviews.remove(currentUserReview);
                } else {
                    canReview = orderService.hasCompletedPurchase(currentUserId, productId);
                }
            }
        }

        model.addAttribute("reviews", allReviews);
        model.addAttribute("currentUserReview", currentUserReview);
        model.addAttribute("canReview", canReview);

        List<Rating> allReviewsForCalculation = ratingRepository.findBySanPham_MaSanPhamOrderByNgayTaoDesc(productId);
        double averageRating = allReviewsForCalculation.stream().mapToInt(Rating::getDiemDanhGia).average().orElse(0.0);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("totalReviews", allReviewsForCalculation.size());

        @SuppressWarnings("unchecked")
        List<Integer> viewedProductIds = (List<Integer>) session.getAttribute("viewedProductIds");
        if (viewedProductIds == null) viewedProductIds = new LinkedList<>();
        viewedProductIds.remove(Integer.valueOf(productId));
        viewedProductIds.add(0, productId);
        if (viewedProductIds.size() > 10) viewedProductIds = viewedProductIds.subList(0, 10);
        session.setAttribute("viewedProductIds", viewedProductIds);

        if (viewedProductIds.size() > 1) {
            List<Integer> idsToFetch = new ArrayList<>(viewedProductIds);
            idsToFetch.remove(Integer.valueOf(productId));
            if (!idsToFetch.isEmpty()) {
                model.addAttribute("recentlyViewedProducts", productRepository.findAllById(idsToFetch));
            } else {
                model.addAttribute("recentlyViewedProducts", Collections.emptyList());
            }
        } else {
            model.addAttribute("recentlyViewedProducts", Collections.emptyList());
        }

        return "user/shop/single-product";
    }

    @GetMapping("/contact")
    public String contactPage() { return "user/general/contact"; }

    @GetMapping("/about-us")
    public String aboutUsPage() { return "user/general/about-us"; }

    @GetMapping("/privacy-policy")
    public String privacyPolicyPage() { return "user/general/privacy-policy"; }

    @GetMapping("/api/product/{id}")
    @ResponseBody
    public ResponseEntity<Product> getProductForQuickView(@PathVariable("id") int productId) {
        Product product = productService.findById(productId);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.notFound().build();
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Product>> liveSearchProducts(@RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        Page<Product> productPage = productService.searchProductsForUser(keyword, 1, 5);
        return ResponseEntity.ok(productPage.getContent());
    }
}