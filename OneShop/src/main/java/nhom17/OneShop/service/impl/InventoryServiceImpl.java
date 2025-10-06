package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Inventory;
import nhom17.OneShop.repository.InventoryRepository;
import nhom17.OneShop.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Page<Inventory> findAll(String keyword, String sort, int page, int size) {
        // Xử lý logic sắp xếp
        Sort sortable = Sort.by("maSanPham").ascending(); // Mặc định
        if (StringUtils.hasText(sort)) {
            switch (sort) {
                case "qty_asc":
                    sortable = Sort.by("soLuongTon").ascending();
                    break;
                case "qty_desc":
                    sortable = Sort.by("soLuongTon").descending();
                    break;
            }
        }
        Pageable pageable = PageRequest.of(page - 1, size, sortable);

        // Xử lý logic tìm kiếm
        Specification<Inventory> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.hasText(keyword)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("sanPham").get("tenSanPham"), "%" + keyword + "%")
            );
        }
        return inventoryRepository.findAll(spec, pageable);
    }
}
