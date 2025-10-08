package nhom17.OneShop.controller;

import nhom17.OneShop.entity.User;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.repository.RoleRepository;
import nhom17.OneShop.request.UserRequest;
import nhom17.OneShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired private RoleRepository roleRepository;
    @Autowired private MembershipTierRepository membershipTierRepository;

    @GetMapping
    public String listUsers(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Integer roleId,
                            @RequestParam(required = false) Integer tierId,
                            @RequestParam(required = false) Integer status,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "5") int size,
                            Model model) {
        Page<User> userPage = userService.findAll(keyword, roleId, tierId, status, page, size);
        model.addAttribute("userPage", userPage);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("tiers", membershipTierRepository.findAll());

        // Giữ lại state cho bộ lọc
        model.addAttribute("keyword", keyword);
        model.addAttribute("roleId", roleId);
        model.addAttribute("tierId", tierId);
        model.addAttribute("status", status);

        return "admin/user/users";
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable int id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/user";
        }
        model.addAttribute("user", user);
        return "admin/user/userDetail";
    }

    @GetMapping({"/add", "/edit/{id}"})
    public String showUserForm(@PathVariable(name = "id", required = false) Integer id, Model model) {
        UserRequest userRequest = new UserRequest();
        if (id != null) { // Chế độ sửa
            User user = userService.findById(id);
            // Chuyển đổi Entity sang DTO
            userRequest.setMaNguoiDung(user.getMaNguoiDung());
            userRequest.setHoTen(user.getHoTen());
            userRequest.setEmail(user.getEmail());
            userRequest.setTenDangNhap(user.getTenDangNhap());
            userRequest.setSoDienThoai(user.getSoDienThoai());
            userRequest.setTrangThai(user.getTrangThai());
            userRequest.setMaVaiTro(user.getVaiTro().getMaVaiTro());
            if(user.getHangThanhVien() != null){
                userRequest.setMaHangThanhVien(user.getHangThanhVien().getMaHangThanhVien());
            }
        }

        model.addAttribute("userRequest", userRequest);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("tiers", membershipTierRepository.findAll());
        return "admin/user/addOrEditUser";
    }

    // ✅ LƯU DỮ LIỆU (THÊM VÀ SỬA)
    @PostMapping("/save")
    public String saveUser(@ModelAttribute UserRequest userRequest, RedirectAttributes redirectAttributes) {
        try {
            userService.save(userRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu người dùng thành công!");
        } catch (DuplicateRecordException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi không mong muốn xảy ra!");
        }
        return "redirect:/admin/user";
    }

    // ✅ XÓA NGƯỜI DÙNG
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa người dùng này, có thể do các ràng buộc dữ liệu.");
        }
        return "redirect:/admin/user";
    }
}
