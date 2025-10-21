package nhom17.OneShop.service;

import nhom17.OneShop.entity.Supplier;
import nhom17.OneShop.request.SupplierRequest;
import org.springframework.data.domain.Page;

public interface SupplierService {
    Page<Supplier> search(String keyword, int page, int size);
    Supplier findById(int id);
    void save(SupplierRequest supplierRequest);
    void delete(int id);
}
