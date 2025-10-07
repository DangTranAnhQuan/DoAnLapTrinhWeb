package nhom17.OneShop.service;

import nhom17.OneShop.entity.Product;
import nhom17.OneShop.request.ProductRequest;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public interface ProductService {
    Page<Product> searchProducts(String keyword, Boolean status, Integer categoryId, Integer brandId, String sort, int page, int size);
    Page<Product> searchUserProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sort, int page, int size);
    Product findById(int id);
    void save(ProductRequest productRequest);
    void delete(int id);
}
