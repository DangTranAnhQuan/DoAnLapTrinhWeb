package nhom17.OneShop.repository;

import nhom17.OneShop.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findByDonHang_MaDonHangOrderByThoiDiemThayDoiDesc(Long orderId);
}
