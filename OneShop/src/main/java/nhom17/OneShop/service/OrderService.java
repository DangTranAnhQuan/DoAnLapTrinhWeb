package nhom17.OneShop.service;

import nhom17.OneShop.entity.Order;
import java.util.List;

public interface OrderService {
    List<Order> findOrdersForCurrentUser();
    Order findOrderByIdForCurrentUser(Long orderId);
}

