package nhom17.OneShop.repository;

import nhom17.OneShop.entity.OrderDetail;
import nhom17.OneShop.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}