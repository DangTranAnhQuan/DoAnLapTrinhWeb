package nhom17.OneShop.repository;

import nhom17.OneShop.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, String> {
    Optional<KhuyenMai> findByMaKhuyenMaiAndTrangThai(String maKhuyenMai, Integer trangThai);
}