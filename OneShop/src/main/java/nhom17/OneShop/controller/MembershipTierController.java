package nhom17.OneShop.controller;

import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.request.MembershipTierRequest;
import nhom17.OneShop.service.MembershipTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/membership-tier")
public class MembershipTierController {

    @Autowired
    private MembershipTierService membershipTierService;

    @GetMapping
    public String listTiers(Model model) {
        List<MembershipTier> tiers = membershipTierService.findAllSorted();
        model.addAttribute("tiers", tiers);
        model.addAttribute("tierRequest", new MembershipTierRequest());
        return "admin/system/membership-tiers";
    }

    @PostMapping("/save")
    public String saveTier(@ModelAttribute MembershipTierRequest request, RedirectAttributes redirectAttributes) {
        membershipTierService.save(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu hạng thành viên thành công!");
        return "redirect:/admin/membership-tier";
    }

    @GetMapping("/delete/{id}")
    public String deleteTier(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            membershipTierService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa hạng thành viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa hạng này vì đang có người dùng sử dụng.");
        }
        return "redirect:/admin/membership-tier";
    }
}
