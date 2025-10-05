package nhom17.OneShop.service;

import nhom17.OneShop.entity.SanPham;
import nhom17.OneShop.request.ProductRequest;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<SanPham> searchProducts(String keyword, Boolean status, String sort, int page, int size);
    SanPham findById(int id);
    void save(ProductRequest productRequest);
    void delete(int id);
}
