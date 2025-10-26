package nhom17.OneShop.service;

import nhom17.OneShop.entity.*;
import nhom17.OneShop.dto.DashboardDataDTO;
import nhom17.OneShop.request.OrderUpdateRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> findOrdersForCurrentUser();
    Order findOrderByIdForCurrentUser(Long orderId);

//    Admin
    Page<Order> findAll(String keyword, String status, String paymentMethod, String paymentStatus, String shippingMethod, int page, int size);
    List<OrderStatusHistory> findHistoryByOrderId(long orderId);
    Order findById(long id);
    public Map<Long, List<ShippingFee>> getCarriersWithFeesByOrder(List<Order> danhSachDonHang);
    void updateLoyaltyPoints(Order order, String oldStatus, String newStatus);
    void update(Long orderId, OrderUpdateRequest request);
    void cancelOrder(Long orderId, User currentUser);

    DashboardDataDTO getDashboardData(int year, int month);
    boolean hasCompletedPurchase(Integer userId, Integer productId);
    boolean canUserReviewProduct(Integer userId, Integer productId);
}
