package nhom17.OneShop.service;

import nhom17.OneShop.entity.Category;
import nhom17.OneShop.request.CategoryRequest;
import org.springframework.data.domain.Page;

public interface CategoryService {
    Page<Category> searchAndFilter(String keyword, Boolean status, int page, int size);
    Category findById(int id);
    void save(CategoryRequest categoryRequest);
    void delete(int id);
}
