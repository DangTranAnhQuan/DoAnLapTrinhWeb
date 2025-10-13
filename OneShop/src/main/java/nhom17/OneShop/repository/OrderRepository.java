package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByNguoiDungOrderByNgayDatDesc(User nguoiDung);
    @Query("SELECT d FROM DonHang d LEFT JOIN FETCH d.donHangChiTiets WHERE d.maDonHang = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);
}