package nhom17.OneShop.controller;

import nhom17.OneShop.entity.DiaChi;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.repository.DiaChiRepository;
import nhom17.OneShop.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MyAccountController {

    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private DiaChiRepository diaChiRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Hiển thị trang My Account chính
    @GetMapping("/my-account")
    public String myAccountPage(Model model, @RequestParam(name = "tab", required = false, defaultValue = "account") String activeTab) {
        NguoiDung currentUser = getCurrentUser();
        List<DiaChi> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        model.addAttribute("user", currentUser);
        model.addAttribute("addresses", addresses);
        model.addAttribute("activeTab", activeTab);
        return "user/account/my-account";
    }

    // Xử lý cập nhật thông tin cá nhân
    @PostMapping("/my-account/update-details")
    public String updateDetails(@ModelAttribute NguoiDung user, RedirectAttributes redirectAttributes) {
        NguoiDung currentUser = getCurrentUser();
        currentUser.setHoTen(user.getHoTen());
        currentUser.setSoDienThoai(user.getSoDienThoai());
        nguoiDungRepository.save(currentUser);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/my-account";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/my-account/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        NguoiDung currentUser = getCurrentUser();
        if (!passwordEncoder.matches(currentPassword, currentUser.getMatKhau())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "redirect:/my-account";
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp.");
            return "redirect:/my-account";
        }
        currentUser.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDungRepository.save(currentUser);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/my-account";
    }

    // Hiển thị form thêm địa chỉ mới
    @GetMapping("/my-account/add-address")
    public String showAddAddressForm(Model model) {
        model.addAttribute("address", new DiaChi());
        return "user/account/address-form";
    }

    // Xử lý lưu địa chỉ mới
    @PostMapping("/my-account/add-address")
    public String saveNewAddress(@ModelAttribute DiaChi address, RedirectAttributes redirectAttributes) {
        NguoiDung currentUser = getCurrentUser();
        address.setNguoiDung(currentUser);
        diaChiRepository.save(address);
        redirectAttributes.addFlashAttribute("success", "Thêm địa chỉ mới thành công!");
        return "redirect:/my-account";
    }

    // Xóa địa chỉ
    @PostMapping("/my-account/delete-address/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes redirectAttributes) {
        NguoiDung currentUser = getCurrentUser();
        DiaChi address = diaChiRepository.findById(addressId).orElseThrow();
        if (address.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
            diaChiRepository.deleteById(addressId);
            redirectAttributes.addFlashAttribute("success", "Xóa địa chỉ thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa địa chỉ này.");
        }
        return "redirect:/my-account";
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}