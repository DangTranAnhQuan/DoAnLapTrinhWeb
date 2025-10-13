package nhom17.OneShop.repository;

import nhom17.OneShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByTenDangNhap(String tenDangNhap);
    @Query("SELECT u FROM NguoiDung u LEFT JOIN FETCH u.hangThanhVien WHERE u.email = :email")
    Optional<User> findByEmailWithMembership(@Param("email") String email);
}