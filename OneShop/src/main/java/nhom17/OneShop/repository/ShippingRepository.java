package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShippingRepository extends JpaRepository<Shipping, Long>, JpaSpecificationExecutor<Shipping> {
    boolean existsByDonHang_MaDonHang(Long maDonHang);
}
