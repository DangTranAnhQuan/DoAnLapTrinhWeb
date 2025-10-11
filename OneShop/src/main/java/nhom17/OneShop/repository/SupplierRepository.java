package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Page<Supplier> findByTenNCCContainingIgnoreCase(String keyword, Pageable pageable);
}
