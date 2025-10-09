package nhom17.OneShop.service;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.request.OrderUpdateRequest;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<Order> findAll(String keyword, String status, String paymentMethod, String paymentStatus, int page, int size);

    Order findById(long id);
    void update(Long orderId, OrderUpdateRequest request, User adminUser);
}
