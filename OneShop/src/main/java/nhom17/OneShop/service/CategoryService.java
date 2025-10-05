package nhom17.OneShop.service;

import nhom17.OneShop.entity.DanhMuc;
import nhom17.OneShop.request.CategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    Page<DanhMuc> searchAndFilter(String keyword, Boolean status, int page, int size);
    DanhMuc findById(int id);
    void save(CategoryRequest categoryRequest);
    void delete(int id);
}
