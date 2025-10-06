package nhom17.OneShop.service;

import nhom17.OneShop.entity.SanPham;
import nhom17.OneShop.request.ProductRequest;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public interface ProductService {
    Page<SanPham> searchProducts(String keyword, Boolean status, String sort, int page, int size);

    Page<SanPham> searchUserProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sort, int page, int size);

    SanPham findById(int id);
    
    void save(ProductRequest productRequest);

    void delete(int id);
}