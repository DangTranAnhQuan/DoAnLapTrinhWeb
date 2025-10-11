package nhom17.OneShop.controller;

import nhom17.OneShop.request.SignUpRequest;
import nhom17.OneShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/sign-in")
    public String showSignInForm() {
        return "user/account/sign-in";
    }

    @GetMapping("/sign-up")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new SignUpRequest());
        return "user/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String processSignUp(@ModelAttribute("user") SignUpRequest signUpRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(signUpRequest);
            redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sign-up";
        }
        return "redirect:/sign-in";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "user/account/forgot-password";
    }
}