package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
    Optional<Voucher> findByMaKhuyenMaiAndTrangThai(String maKhuyenMai, Integer trangThai);
}