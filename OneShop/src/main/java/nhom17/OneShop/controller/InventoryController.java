package nhom17.OneShop.controller;

import nhom17.OneShop.entity.Inventory;
import nhom17.OneShop.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/inventory")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping
    public String listInventory(@RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Specification<Inventory> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.hasText(keyword)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("sanPham").get("tenSanPham"), "%" + keyword + "%")
            );
        }

        Page<Inventory> inventoryPage = inventoryRepository.findAll(spec, pageable);
        model.addAttribute("inventoryPage", inventoryPage);
        model.addAttribute("keyword", keyword);
        return "admin/warehouse/inventory";
    }
}
