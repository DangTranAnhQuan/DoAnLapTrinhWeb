package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VoucherRepository extends JpaRepository<Voucher, String>, JpaSpecificationExecutor<Voucher> {
    boolean existsByTenChienDichIgnoreCase(String tenChienDich);
}
