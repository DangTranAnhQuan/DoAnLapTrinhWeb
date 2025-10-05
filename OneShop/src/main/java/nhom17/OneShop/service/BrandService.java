package nhom17.OneShop.service;

import nhom17.OneShop.entity.ThuongHieu;
import nhom17.OneShop.request.BrandRequest;
import org.springframework.data.domain.Page;

public interface BrandService {
    Page<ThuongHieu> searchAndFilter(String keyword, Boolean status, int page, int size);
    ThuongHieu findById(int id);
    void save(BrandRequest brandRequest);
    void delete(int id);
}
