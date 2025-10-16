//package nhom17.OneShop.controller;
//
//import jakarta.servlet.http.HttpSession;
//import nhom17.OneShop.entity.User;
//import nhom17.OneShop.repository.UserRepository;
//import nhom17.OneShop.service.OtpService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@Controller
//public class ForgotPasswordController {
//
//    private static final String ATTR_OTP_VERIFIED = "otpVerified";
//
//    @Autowired private UserRepository nguoiDungRepository;
//    @Autowired private OtpService otpService;
//    @Autowired private PasswordEncoder passwordEncoder;
//
//    /* =================== PAGES =================== */
//
//    /** Trang đặt mật khẩu mới (chỉ vào khi đã verify OTP và vẫn còn hiệu lực) */
//    @GetMapping("/reset-password")
//    public String resetPasswordPage(HttpSession session) {
//        String email = otpService.getEmail(session);
//        Boolean verified = (Boolean) session.getAttribute(ATTR_OTP_VERIFIED);
//
//        // Chưa verify / OTP hết hạn / chưa có email => quay lại forgot
//        if (email == null || otpService.isExpired(session) || verified == null || !verified) {
//            return "redirect:/forgot-password";
//        }
//        return "user/account/reset-password";
//    }
//
//    /* =================== APIs =================== */
//
//    /** Gửi OTP – chỉ bắt đầu đếm ngược khi API trả OK */
//    @PostMapping("/forgot-password/send-otp")
//    @ResponseBody
//    public ResponseEntity<?> sendOtp(@RequestParam("email") String email, HttpSession session) {
//        var user = nguoiDungRepository.findByEmail(email).orElse(null);
//        if (user == null) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Email không tồn tại"));
//        }
//        try {
//            otpService.generateAndSend(email, session);
//            // SỬA Ở ĐÂY: Đổi tên phương thức
//            return ResponseEntity.ok(Map.of("ok", true, "expiresIn", otpService.getExpireSeconds()));
//        } catch (Exception ex) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Gửi OTP thất bại: " + ex.getMessage()));
//        }
//    }
//
//
//    /** Xác thực OTP */
//    @PostMapping("/forgot-password/verify-otp")
//    @ResponseBody
//    public ResponseEntity<?> verifyOtp(@RequestParam("otp") String otp, HttpSession session) {
//        if (otpService.getEmail(session) == null) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Chưa nhập email"));
//        }
//        if (otpService.isExpired(session)) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "OTP đã hết hạn"));
//        }
//        if (!otpService.verify(otp, session)) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "OTP không đúng"));
//        }
//
//        // Đánh dấu đã verify
//        session.setAttribute(ATTR_OTP_VERIFIED, true);
//        return ResponseEntity.ok(Map.of("ok", true));
//    }
//
//    /** Gửi lại OTP */
//    @PostMapping("/forgot-password/resend-otp")
//    @ResponseBody
//    public ResponseEntity<?> resendOtp(HttpSession session) {
//        String email = otpService.getEmail(session);
//        if (email == null) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Chưa có email để gửi OTP"));
//        }
//
//        // Gửi lại OTP đồng thời reset trạng thái verified
//        session.removeAttribute(ATTR_OTP_VERIFIED);
//
//        otpService.generateAndSend(email, session);
//        // SỬA Ở ĐÂY: Đổi tên phương thức
//        return ResponseEntity.ok(Map.of(
//                "ok", true,
//                "expiresIn", otpService.getExpireSeconds()
//        ));
//    }
//
//    /** Lưu mật khẩu mới (chỉ cho khi đã verify + còn hạn) */
//    @PostMapping("/reset-password/submit")
//    public String doReset(@RequestParam String password,
//                          @RequestParam String confirm,
//                          HttpSession session,
//                          Model model) {
//        String email = otpService.getEmail(session);
//        Boolean verified = (Boolean) session.getAttribute(ATTR_OTP_VERIFIED);
//
//        if (email == null || otpService.isExpired(session) || verified == null || !verified) {
//            return "redirect:/forgot-password";
//        }
//        if (!password.equals(confirm)) {
//            model.addAttribute("error", "Mật khẩu nhập lại không khớp");
//            return "user/account/reset-password";
//        }
//
//        User user = nguoiDungRepository.findByEmail(email).orElse(null);
//        if (user == null) {
//            return "redirect:/forgot-password";
//        }
//
//        user.setMatKhau(passwordEncoder.encode(password));
//        nguoiDungRepository.save(user);
//
//        // Dọn sạch session OTP
//        otpService.clear(session);
//        session.removeAttribute(ATTR_OTP_VERIFIED);
//
//        return "redirect:/sign-in?reset=success";
//    }
//}
//
package nhom17.OneShop.controller;

import nhom17.OneShop.service.OtpService;
import nhom17.OneShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ForgotPasswordController {

    // --- Các dependency cần thiết ---
    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    // --- Các hàm xử lý quên mật khẩu ---

    /**
     * Hiển thị trang nhập email để bắt đầu quá trình quên mật khẩu.
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "user/account/forgot-password";
    }

    /**
     * Xử lý việc gửi email, tạo và gửi mã OTP.
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        try {
            userService.sendResetPasswordOtp(email);
            model.addAttribute("email", email);
            return "user/account/verify-reset-password";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgot-password";
        }
    }

    /**
     * Hiển thị trang xác thực OTP.
     */
    @GetMapping("/verify-reset-password")
    public String showVerifyResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "user/account/verify-reset-password";
    }

    /**
     * Xử lý xác thực mã OTP người dùng nhập vào.
     */
    @PostMapping("/verify-reset-password")
    public String verifyResetPasswordOtp(@RequestParam("email") String email,
                                         @RequestParam("otp") String otp,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        try {
            boolean isValid = userService.verifyResetPasswordOtp(email, otp);
            if (isValid) {
                model.addAttribute("email", email);
                return "user/account/reset-password";
            } else {
                redirectAttributes.addFlashAttribute("error", "Mã OTP không đúng hoặc đã hết hạn!");
                redirectAttributes.addFlashAttribute("email", email);
                return "redirect:/verify-reset-password?email=" + email;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/verify-reset-password?email=" + email;
        }
    }

    /**
     * Hiển thị trang đặt lại mật khẩu mới (sau khi đã xác thực OTP thành công).
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "user/account/reset-password";
    }

    /**
     * Xử lý việc lưu mật khẩu mới.
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("email") String email,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("Mật khẩu xác nhận không khớp!");
            }
            if (newPassword.length() < 6) {
                throw new RuntimeException("Mật khẩu phải có ít nhất 6 ký tự!");
            }
            userService.resetPassword(email, newPassword);
            redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            return "redirect:/sign-in";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/reset-password?email=" + email;
        }
    }

    /**
     * Xử lý yêu cầu gửi lại mã OTP.
     */
    @GetMapping("/resend-reset-otp")
    public String resendResetOtp(@RequestParam("email") String email,
                                 RedirectAttributes redirectAttributes) {
        try {
            otpService.generateOtpForEmail(email, "Quên mật khẩu");
            redirectAttributes.addFlashAttribute("success", "Đã gửi lại mã OTP. Vui lòng kiểm tra email!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể gửi lại OTP. Vui lòng thử lại!");
        }
        return "redirect:/verify-reset-password?email=" + email;
    }
}