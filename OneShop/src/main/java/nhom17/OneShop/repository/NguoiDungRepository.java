package nhom17.OneShop.repository;

import nhom17.OneShop.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    @Query("SELECT u FROM NguoiDung u LEFT JOIN FETCH u.hangThanhVien WHERE u.email = :email")
    Optional<NguoiDung> findByEmailWithMembership(@Param("email") String email);
}