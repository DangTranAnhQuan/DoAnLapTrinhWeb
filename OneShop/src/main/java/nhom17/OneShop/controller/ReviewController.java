package nhom17.OneShop.controller;

import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.entity.Rating;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class ReviewController {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/submit-review")
    public String submitReview(@RequestParam("productId") Integer productId,
                               @RequestParam("rating") Integer ratingScore,
                               @RequestParam("comment") String comment,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đánh giá.");
            return "redirect:/product/" + productId;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        NguoiDung currentUser = nguoiDungRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));

        Rating rating = new Rating();
        rating.setNguoiDung(currentUser);
        rating.setSanPham(product);
        rating.setDiemDanhGia(ratingScore);
        rating.setBinhLuan(comment);
        rating.setNgayTao(LocalDateTime.now());

        ratingRepository.save(rating);

        redirectAttributes.addFlashAttribute("success", "Cảm ơn bạn đã gửi đánh giá!");
        return "redirect:/product/" + productId;
    }
}