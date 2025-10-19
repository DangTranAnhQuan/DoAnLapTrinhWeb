package nhom17.OneShop.repository;

import nhom17.OneShop.entity.ShippingCarrier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingCarrierRepository extends JpaRepository<ShippingCarrier, Integer> {
    Page<ShippingCarrier> findByTenNVCContainingIgnoreCase(String keyword, Pageable pageable);
    Optional<ShippingCarrier> findByTenNVCIgnoreCase(String tenNVC);
    boolean existsByTenNVCIgnoreCase(String tenNVC);
    boolean existsByTenNVCIgnoreCaseAndMaNVCNot(String tenNVC, Integer maNVC);
}
