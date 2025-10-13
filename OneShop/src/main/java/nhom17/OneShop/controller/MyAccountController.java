//package nhom17.OneShop.controller;
//
//import nhom17.OneShop.entity.DiaChi;
//import nhom17.OneShop.entity.NguoiDung;
//import nhom17.OneShop.repository.DiaChiRepository;
//import nhom17.OneShop.repository.NguoiDungRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.List;
//
//@Controller
//public class MyAccountController {
//
//    @Autowired private NguoiDungRepository nguoiDungRepository;
//    @Autowired private DiaChiRepository diaChiRepository;
//    @Autowired private PasswordEncoder passwordEncoder;
//
//    // Hiển thị trang My Account (giữ tab-panel), chọn tab qua ?tab=...
//    @GetMapping("/my-account")
//    public String myAccountPage(Model model,
//                                @RequestParam(name = "tab", required = false, defaultValue = "account")
//                                String activeTab) {
//        NguoiDung currentUser = getCurrentUser();
//        List<DiaChi> addresses =
//                diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
//        model.addAttribute("user", currentUser);
//        model.addAttribute("addresses", addresses);
//        model.addAttribute("activeTab", activeTab);
//        return "user/account/my-account";
//    }
//
//    // URL riêng cho tab Địa chỉ (vẫn dùng cùng template tab-panel)
//    @GetMapping("/my-account/addresses")
//    public String addressesTab() {
//        return "redirect:/my-account?tab=addresses";
//    }
//
//    // ====== ACCOUNT ACTIONS (ở tab 'account') ======
//    @PostMapping("/my-account/update-details")
//    public String updateDetails(@ModelAttribute NguoiDung user,
//                                RedirectAttributes redirectAttributes) {
//        NguoiDung currentUser = getCurrentUser();
//        currentUser.setHoTen(user.getHoTen());
//        currentUser.setSoDienThoai(user.getSoDienThoai());
//        nguoiDungRepository.save(currentUser);
//        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
//        return "redirect:/my-account?tab=account";
//    }
//
//    @PostMapping("/my-account/change-password")
//    public String changePassword(@RequestParam String currentPassword,
//                                 @RequestParam String newPassword,
//                                 @RequestParam String confirmPassword,
//                                 RedirectAttributes redirectAttributes) {
//        NguoiDung currentUser = getCurrentUser();
//        if (!passwordEncoder.matches(currentPassword, currentUser.getMatKhau())) {
//            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
//            return "redirect:/my-account?tab=account";
//        }
//        if (!newPassword.equals(confirmPassword)) {
//            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp.");
//            return "redirect:/my-account?tab=account";
//        }
//        currentUser.setMatKhau(passwordEncoder.encode(newPassword));
//        nguoiDungRepository.save(currentUser);
//        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
//        return "redirect:/my-account?tab=account";
//    }
//
//    // ====== ADDRESS PAGES & ACTIONS (ở tab 'addresses') ======
//    @GetMapping("/my-account/add-address")
//    public String showAddAddressForm(Model model) {
//        model.addAttribute("address", new DiaChi());
//        return "user/account/address-form";
//    }
//
//    @PostMapping("/my-account/add-address")
//    public String saveNewAddress(@ModelAttribute DiaChi address,
//                                 RedirectAttributes redirectAttributes) {
//        NguoiDung currentUser = getCurrentUser();
//        address.setNguoiDung(currentUser);
//        diaChiRepository.save(address);
//        redirectAttributes.addFlashAttribute("success", "Thêm địa chỉ mới thành công!");
//        return "redirect:/my-account?tab=addresses";
//    }
//
//    @PostMapping("/my-account/delete-address/{id}")
//    public String deleteAddress(@PathVariable("id") Integer addressId,
//                                RedirectAttributes redirectAttributes) {
//        NguoiDung currentUser = getCurrentUser();
//        DiaChi address = diaChiRepository.findById(addressId).orElseThrow();
//        if (address.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
//            diaChiRepository.deleteById(addressId);
//            redirectAttributes.addFlashAttribute("success", "Xóa địa chỉ thành công!");
//        } else {
//            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xóa địa chỉ này.");
//        }
//        return "redirect:/my-account?tab=addresses";
//    }
//
//    private NguoiDung getCurrentUser() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = ((UserDetails) principal).getUsername();
//        return nguoiDungRepository.findByEmail(username).orElseThrow();
//    }
//}
package nhom17.OneShop.controller;

import nhom17.OneShop.entity.DiaChi;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.repository.DiaChiRepository;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MyAccountController {

    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private DiaChiRepository diaChiRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private StorageService storageService;

    // My Account (tab-panel)
    @GetMapping("/my-account")
    public String myAccountPage(Model model,
                                @RequestParam(name = "tab", required = false, defaultValue = "account") String activeTab) {
        NguoiDung currentUser = getCurrentUser();
        List<DiaChi> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        model.addAttribute("user", currentUser);
        model.addAttribute("addresses", addresses);
        model.addAttribute("activeTab", activeTab);
        return "user/account/my-account";
    }

    // Update profile (ở tab account)
    @PostMapping("/my-account/update-details")
    public String updateDetails(@ModelAttribute NguoiDung user, RedirectAttributes ra) {
        NguoiDung currentUser = getCurrentUser();
        currentUser.setHoTen(user.getHoTen());
        currentUser.setSoDienThoai(user.getSoDienThoai());
        nguoiDungRepository.save(currentUser);
        ra.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/my-account?tab=account";
    }

    // Change password (ở tab account)
    @PostMapping("/my-account/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        NguoiDung currentUser = getCurrentUser();
        if (!passwordEncoder.matches(currentPassword, currentUser.getMatKhau())) {
            ra.addFlashAttribute("error", "Mật khẩu hiện tại không đúng.");
            return "redirect:/my-account?tab=account";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Mật khẩu mới không khớp.");
            return "redirect:/my-account?tab=account";
        }
        currentUser.setMatKhau(passwordEncoder.encode(newPassword));
        nguoiDungRepository.save(currentUser);
        ra.addFlashAttribute("success", "Đổi mật khẩu thành công!");
        return "redirect:/my-account?tab=account";
    }

    // Add address form
    @GetMapping("/my-account/add-address")
    public String showAddAddressForm(Model model) {
        model.addAttribute("address", new DiaChi());
        return "user/account/address-form";
    }

    // Save address (ở tab addresses)
    @PostMapping("/my-account/add-address")
    public String saveNewAddress(@ModelAttribute DiaChi address, RedirectAttributes ra) {
        NguoiDung currentUser = getCurrentUser();
        address.setNguoiDung(currentUser);
        diaChiRepository.save(address);
        ra.addFlashAttribute("success", "Thêm địa chỉ mới thành công!");
        return "redirect:/my-account?tab=addresses";
    }

    // Delete address (ở tab addresses)
    @PostMapping("/my-account/delete-address/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra) {
        NguoiDung currentUser = getCurrentUser();
        DiaChi address = diaChiRepository.findById(addressId).orElseThrow();
        if (address.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
            diaChiRepository.deleteById(addressId);
            ra.addFlashAttribute("success", "Xóa địa chỉ thành công!");
        } else {
            ra.addFlashAttribute("error", "Bạn không có quyền xóa địa chỉ này.");
        }
        return "redirect:/my-account?tab=addresses";
    }

    /**
     * Xử lý việc cập nhật ảnh đại diện.
     */
    @PostMapping("/my-account/update-avatar")
    public String updateAvatar(@RequestParam("avatarFile") MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        if (avatarFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn một file ảnh.");
            return "redirect:/my-account?tab=account";
        }

        try {
            NguoiDung currentUser = getCurrentUser();

            // Lưu file mới vào thư mục 'avatars'
            String fileName = storageService.storeFile(avatarFile, "avatars");

            // Xóa file ảnh cũ nếu có
            if (currentUser.getAnhDaiDien() != null && !currentUser.getAnhDaiDien().isEmpty()) {
                storageService.deleteFile("avatars/" + currentUser.getAnhDaiDien());
            }

            // Cập nhật đường dẫn ảnh mới cho người dùng và lưu vào CSDL
            currentUser.setAnhDaiDien(fileName);
            nguoiDungRepository.save(currentUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải lên ảnh: " + e.getMessage());
        }

        return "redirect:/my-account?tab=account";
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}
