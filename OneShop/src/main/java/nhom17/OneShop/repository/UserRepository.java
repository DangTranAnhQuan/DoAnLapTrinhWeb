package nhom17.OneShop.repository;

import nhom17.OneShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Optional<User> findByTenDangNhapIgnoreCase(String tenDangNhap);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByTenDangNhapIgnoreCase(String tenDangNhap);
    boolean existsByEmailIgnoreCaseAndMaNguoiDungNot(String email, Integer userId);
    boolean existsByTenDangNhapIgnoreCaseAndMaNguoiDungNot(String tenDangNhap, Integer userId);

    boolean existsByVaiTro_MaVaiTro(Integer roleId);
    // Kiểm tra xem có User nào đang dùng một MembershipTier ID cụ thể không
    boolean existsByHangThanhVien_MaHangThanhVien(Integer tierId);
}