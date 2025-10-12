package nhom17.OneShop.service;

import nhom17.OneShop.entity.DonHang;
import java.util.List;

public interface OrderService {
    List<DonHang> findOrdersForCurrentUser();
    DonHang findOrderByIdForCurrentUser(Long orderId);
}

