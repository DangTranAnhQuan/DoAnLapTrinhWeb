package nhom17.OneShop.repository;

import nhom17.OneShop.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Integer> {
    Optional<MembershipTier> findByTenHangIgnoreCase(String tenHang);
    Optional<MembershipTier> findByDiemToiThieu(Integer diemToiThieu);
    boolean existsByTenHangIgnoreCase(String tenHang);
    boolean existsByDiemToiThieu(Integer diemToiThieu);
    boolean existsByTenHangIgnoreCaseAndMaHangThanhVienNot(String tenHang, Integer id);
    boolean existsByDiemToiThieuAndMaHangThanhVienNot(Integer diemToiThieu, Integer id);
}
