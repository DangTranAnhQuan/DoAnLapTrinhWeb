//package nhom17.OneShop.controller;
//
//import jakarta.servlet.http.HttpSession;
//import nhom17.OneShop.entity.NguoiDung;
//import nhom17.OneShop.repository.NguoiDungRepository;
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
//    @Autowired private NguoiDungRepository nguoiDungRepository;
//    @Autowired private OtpService otpService;
//    @Autowired private PasswordEncoder passwordEncoder;
//
//     Trang nhập email + OTP
//    @GetMapping("/forgot-password")
//    public String forgotPasswordPage() {
//        return "user/account/forgot-password";
//    }
//
//    // Gửi OTP: chỉ chạy thành công mới cho phép bắt đầu đếm ngược
//    @PostMapping("/forgot-password/send-otp")
//    @ResponseBody
//    public ResponseEntity<?> sendOtp(@RequestParam("email") String email, HttpSession session) {
//        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
//        if (user == null) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Email không tồn tại"));
//        }
//        otpService.generateAndSend(email, session);
//        return ResponseEntity.ok(Map.of(
//                "ok", true,
//                "expiresIn", otpService.expireSeconds()
//        ));
//    }
//
//    // Xác thực OTP
//    @PostMapping("/forgot-password/verify-otp")
//    @ResponseBody
//    public ResponseEntity<?> verifyOtp(@RequestParam("otp") String otp, HttpSession session) {
//        if (otpService.isExpired(session)) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "OTP đã hết hạn"));
//        }
//        if (!otpService.verify(otp, session)) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "OTP không đúng"));
//        }
//        return ResponseEntity.ok(Map.of("ok", true));
//    }
//
//    // Gửi lại OTP
//    @PostMapping("/forgot-password/resend-otp")
//    @ResponseBody
//    public ResponseEntity<?> resendOtp(HttpSession session) {
//        String email = otpService.getEmail(session);
//        if (email == null) {
//            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Chưa có email"));
//        }
//        otpService.generateAndSend(email, session);
//        return ResponseEntity.ok(Map.of("ok", true, "expiresIn", otpService.expireSeconds()));
//    }
//
//    // Trang đặt mật khẩu mới (đã verify OTP xong rồi mới cho vào trang này)
//    @GetMapping("/reset-password")
//    public String resetPasswordPage(Model model, HttpSession session) {
//        // Nếu chưa verify thì chặn
//        if (otpService.isExpired(session) || otpService.getEmail(session) == null) {
//            return "redirect:/forgot-password";
//        }
//        return "user/account/reset-password";
//    }
//
//    // Lưu mật khẩu mới
//    @PostMapping("/reset-password/submit")
//    public String doReset(@RequestParam String password,
//                          @RequestParam String confirm,
//                          HttpSession session,
//                          Model model) {
//        if (!password.equals(confirm)) {
//            model.addAttribute("error", "Mật khẩu không khớp");
//            return "user/account/reset-password";
//        }
//        String email = otpService.getEmail(session);
//        if (email == null) {
//            return "redirect:/forgot-password";
//        }
//        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
//        if (user == null) {
//            return "redirect:/forgot-password";
//        }
//        user.setMatKhau(passwordEncoder.encode(password));
//        nguoiDungRepository.save(user);
//        otpService.clear(session);
//        return "redirect:/sign-in?reset=success";
//    }
//}

package nhom17.OneShop.controller;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class ForgotPasswordController {

    private static final String ATTR_OTP_VERIFIED = "otpVerified";

    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private OtpService otpService;
    @Autowired private PasswordEncoder passwordEncoder;

    /* =================== PAGES =================== */

    /** Trang đặt mật khẩu mới (chỉ vào khi đã verify OTP và vẫn còn hiệu lực) */
    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session) {
        String email = otpService.getEmail(session);
        Boolean verified = (Boolean) session.getAttribute(ATTR_OTP_VERIFIED);

        // Chưa verify / OTP hết hạn / chưa có email => quay lại forgot
        if (email == null || otpService.isExpired(session) || verified == null || !verified) {
            return "redirect:/forgot-password";
        }
        return "user/account/reset-password";
    }

    /* =================== APIs =================== */

    /** Gửi OTP – chỉ bắt đầu đếm ngược khi API trả OK */
    @PostMapping("/forgot-password/send-otp")
    @ResponseBody
    public ResponseEntity<?> sendOtp(@RequestParam("email") String email, HttpSession session) {
        var user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Email không tồn tại"));
        }
        try {
            otpService.generateAndSend(email, session);
            // SỬA Ở ĐÂY: Đổi tên phương thức
            return ResponseEntity.ok(Map.of("ok", true, "expiresIn", otpService.getExpireSeconds()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Gửi OTP thất bại: " + ex.getMessage()));
        }
    }


    /** Xác thực OTP */
    @PostMapping("/forgot-password/verify-otp")
    @ResponseBody
    public ResponseEntity<?> verifyOtp(@RequestParam("otp") String otp, HttpSession session) {
        if (otpService.getEmail(session) == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Chưa nhập email"));
        }
        if (otpService.isExpired(session)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "OTP đã hết hạn"));
        }
        if (!otpService.verify(otp, session)) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "OTP không đúng"));
        }

        // Đánh dấu đã verify
        session.setAttribute(ATTR_OTP_VERIFIED, true);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** Gửi lại OTP */
    @PostMapping("/forgot-password/resend-otp")
    @ResponseBody
    public ResponseEntity<?> resendOtp(HttpSession session) {
        String email = otpService.getEmail(session);
        if (email == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "Chưa có email để gửi OTP"));
        }

        // Gửi lại OTP đồng thời reset trạng thái verified
        session.removeAttribute(ATTR_OTP_VERIFIED);

        otpService.generateAndSend(email, session);
        // SỬA Ở ĐÂY: Đổi tên phương thức
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "expiresIn", otpService.getExpireSeconds()
        ));
    }

    /** Lưu mật khẩu mới (chỉ cho khi đã verify + còn hạn) */
    @PostMapping("/reset-password/submit")
    public String doReset(@RequestParam String password,
                          @RequestParam String confirm,
                          HttpSession session,
                          Model model) {
        String email = otpService.getEmail(session);
        Boolean verified = (Boolean) session.getAttribute(ATTR_OTP_VERIFIED);

        if (email == null || otpService.isExpired(session) || verified == null || !verified) {
            return "redirect:/forgot-password";
        }
        if (!password.equals(confirm)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp");
            return "user/account/reset-password";
        }

        NguoiDung user = nguoiDungRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/forgot-password";
        }

        user.setMatKhau(passwordEncoder.encode(password));
        nguoiDungRepository.save(user);

        // Dọn sạch session OTP
        otpService.clear(session);
        session.removeAttribute(ATTR_OTP_VERIFIED);

        return "redirect:/sign-in?reset=success";
    }
}

