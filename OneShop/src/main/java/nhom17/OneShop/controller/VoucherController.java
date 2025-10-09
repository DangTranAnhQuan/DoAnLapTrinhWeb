package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Voucher;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.request.VoucherRequest;
import nhom17.OneShop.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public String listVouchers(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Integer status,
                               @RequestParam(required = false) Integer kieuApDung,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        Page<Voucher> voucherPage = voucherService.findAll(keyword, status, kieuApDung, page, size);
        model.addAttribute("voucherPage", voucherPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("kieuApDung", kieuApDung);
        return "admin/orders/vouchers";
    }

    @GetMapping("/{id}")
    public String viewVoucher(@PathVariable String id, Model model) {
        model.addAttribute("voucher", voucherService.findById(id));
        return "admin/orders/voucherDetail";
    }

    @GetMapping({"/add", "/edit/{id}"})
    public String showVoucherForm(@PathVariable(required = false) String id,
                                  Model model,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "5") int size,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) Integer kieuApDung) {
        VoucherRequest request = new VoucherRequest();
        if (id != null) {
            Voucher voucher = voucherService.findById(id);
            request.setMaKhuyenMai(voucher.getMaKhuyenMai());
            request.setTenChienDich(voucher.getTenChienDich());
            request.setKieuApDung(voucher.getKieuApDung());
            request.setGiaTri(voucher.getGiaTri());
            request.setBatDauLuc(voucher.getBatDauLuc());
            request.setKetThucLuc(voucher.getKetThucLuc());
            request.setTongTienToiThieu(voucher.getTongTienToiThieu());
            request.setGiamToiDa(voucher.getGiamToiDa());
            request.setGioiHanTongSoLan(voucher.getGioiHanTongSoLan());
            request.setGioiHanMoiNguoi(voucher.getGioiHanMoiNguoi());
            request.setTrangThai(voucher.getTrangThai());
        }
        model.addAttribute("voucherRequest", request);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("kieuApDung", kieuApDung);
        return "admin/orders/addOrEditVoucher";
    }

    @PostMapping("/save")
    public String saveVoucher(@ModelAttribute("voucherRequest") VoucherRequest request,
                              Model model,
                              RedirectAttributes redirectAttributes,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "5") int size,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) Integer kieuApDung) {
        try {
            voucherService.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu khuyến mãi thành công!");
            redirectAttributes.addAttribute("page", page)
                    .addAttribute("size", size)
                    .addAttribute("keyword", keyword)
                    .addAttribute("status", status)
                    .addAttribute("kieuApDung", kieuApDung);
            return "redirect:/admin/voucher";
        } catch (DuplicateRecordException | IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/order/voucherForm";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteVoucher(@PathVariable String id, RedirectAttributes redirectAttributes,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int size,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer status,
                                @RequestParam(required = false) Integer kieuApDung) {
        try {
            voucherService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa khuyến mãi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa khuyến mãi này.");
        }
        redirectAttributes.addAttribute("page", page)
                .addAttribute("size", size)
                .addAttribute("keyword", keyword)
                .addAttribute("status", status)
                .addAttribute("kieuApDung", kieuApDung);
        return "redirect:/admin/voucher";
    }
}