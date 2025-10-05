package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Supplier;
import nhom17.OneShop.repository.SupplierRepository;
import nhom17.OneShop.request.SupplierRequest;
import nhom17.OneShop.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public Page<Supplier> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maNCC").ascending());
        if (StringUtils.hasText(keyword)) {
            return supplierRepository.findByTenNCCContainingIgnoreCase(keyword, pageable);
        }
        return supplierRepository.findAll(pageable);
    }

    @Override
    public Supplier findById(int id) {
        return supplierRepository.findById(id).orElse(null);
    }

    @Override
    public void save(SupplierRequest supplierRequest) {
        Supplier supplier = new Supplier();
        // Nếu có ID, đây là trường hợp cập nhật
        if (supplierRequest.getMaNCC() != null) {
            supplier = supplierRepository.findById(supplierRequest.getMaNCC())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));
        }
        supplier.setTenNCC(supplierRequest.getTenNCC());
        supplier.setSdt(supplierRequest.getSdt());
        supplier.setDiaChi(supplierRequest.getDiaChi());
        supplierRepository.save(supplier);
    }

    @Override
    public void delete(int id) {
        supplierRepository.deleteById(id);
    }
}
