package nhom17.OneShop.controller.user;

import nhom17.OneShop.entity.Address;
import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.entity.Order; // Thêm import
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.AddressRepository;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.service.OrderService; // Thêm import
import nhom17.OneShop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

    @Autowired private UserRepository nguoiDungRepository;
    @Autowired private AddressRepository diaChiRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private StorageService storageService;
    @Autowired private OrderService orderService;
    @Autowired private MembershipTierRepository membershipTierRepository;

    @GetMapping("/my-membership")
    public String membershipPage(Model model) {
        User currentUser = getCurrentUser();
        List<MembershipTier> allTiers = membershipTierRepository.findAll(Sort.by("diemToiThieu").ascending());

        model.addAttribute("user", currentUser);
        model.addAttribute("allTiers", allTiers);
        return "user/account/membership";
    }

    @GetMapping("/my-account")
    public String myAccountPage(Model model,
                                @RequestParam(name = "tab", required = false, defaultValue = "account") String activeTab) {
        User currentUser = getCurrentUser();
        List<Address> addresses = diaChiRepository.findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        model.addAttribute("user", currentUser);
        model.addAttribute("addresses", addresses);
        model.addAttribute("activeTab", activeTab);
        return "user/account/my-account";
    }

    @PostMapping("/my-account/update-details")
    public String updateDetails(@ModelAttribute User user, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        currentUser.setHoTen(user.getHoTen());
        currentUser.setSoDienThoai(user.getSoDienThoai());
        nguoiDungRepository.save(currentUser);
        ra.addFlashAttribute("success", "Cập nhật thông tin thành công!");
        return "redirect:/my-account?tab=account";
    }

    @PostMapping("/my-account/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        User currentUser = getCurrentUser();
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

    @GetMapping("/my-account/add-address")
    public String showAddAddressForm(Model model) {
        model.addAttribute("address", new Address());
        return "user/account/address-form";
    }

    @PostMapping("/my-account/add-address")
    public String saveNewAddress(@ModelAttribute Address address, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        address.setNguoiDung(currentUser);
        diaChiRepository.save(address);
        ra.addFlashAttribute("success", "Thêm địa chỉ mới thành công!");
        return "redirect:/my-account?tab=addresses";
    }

    @PostMapping("/my-account/delete-address/{id}")
    public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        Address address = diaChiRepository.findById(addressId).orElseThrow();
        if (address.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
            diaChiRepository.deleteById(addressId);
            ra.addFlashAttribute("success", "Xóa địa chỉ thành công!");
        } else {
            ra.addFlashAttribute("error", "Bạn không có quyền xóa địa chỉ này.");
        }
        return "redirect:/my-account?tab=addresses";
    }

    @PostMapping("/my-account/update-avatar")
    public String updateAvatar(@RequestParam("avatarFile") MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        if (avatarFile.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn một file ảnh.");
            return "redirect:/my-account?tab=account";
        }

        try {
            User currentUser = getCurrentUser();
            String fileName = storageService.storeFile(avatarFile, "avatars");
            if (currentUser.getAnhDaiDien() != null && !currentUser.getAnhDaiDien().isEmpty()) {
                storageService.deleteFile("avatars/" + currentUser.getAnhDaiDien());
            }
            currentUser.setAnhDaiDien(fileName);
            nguoiDungRepository.save(currentUser);
            redirectAttributes.addFlashAttribute("success", "Cập nhật ảnh đại diện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tải lên ảnh: " + e.getMessage());
        }
        return "redirect:/my-account?tab=account";
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {
        int size = 5;
        User currentUser = getCurrentUser();

        Page<Order> orderPage = orderService.findOrdersForCurrentUser(page, size);

        model.addAttribute("user", currentUser);
        model.addAttribute("orderPage", orderPage);

        return "user/account/my-orders";
    }

    @GetMapping("/order-details/{id}")
    public String orderDetails(@PathVariable("id") Long id, Model model) {
        try {
            Order order = orderService.findOrderByIdForCurrentUser(id);
            model.addAttribute("order", order);
            return "user/account/order-details";
        } catch (Exception e) {
            return "redirect:/my-orders";
        }
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
    @PostMapping("/my-orders/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Long orderId, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser();
            orderService.cancelOrder(orderId, currentUser);
            redirectAttributes.addFlashAttribute("success", "Đã hủy đơn hàng #" + orderId + " thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/my-orders";
    }
}