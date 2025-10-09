package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.OrderStatusHistory;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.OrderRepository;
import nhom17.OneShop.repository.OrderStatusHistoryRepository;
import nhom17.OneShop.request.OrderUpdateRequest;
import nhom17.OneShop.service.OrderService;
import nhom17.OneShop.specification.OrderSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Override
    public Page<Order> findAll(String keyword, String status, String paymentMethod, String paymentStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayTao").descending());

        return orderRepository.findAll(
                OrderSpecification.filterOrders(keyword, status, paymentMethod, paymentStatus),
                pageable
        );
    }

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void update(Long orderId, OrderUpdateRequest request, User adminUser) {
        // 1. Tìm đơn hàng trong CSDL
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        String oldStatus = order.getTrangThai();
        String newStatus = request.getTrangThai();

        // 2. Kiểm tra xem trạng thái có thực sự thay đổi không
        if (!Objects.equals(oldStatus, newStatus)) {
            // 3. Nếu có, tạo một bản ghi lịch sử mới
            OrderStatusHistory history = new OrderStatusHistory();
            history.setDonHang(order);

            history.setTuTrangThai(oldStatus);
            history.setDenTrangThai(newStatus);
            history.setThoiDiemThayDoi(LocalDateTime.now());
            history.setNguoiThucHien(adminUser); // Gán admin đã thực hiện
            // Tùy chọn: Thêm ghi chú
            // history.setGhiChu("Admin đã cập nhật trạng thái.");

            historyRepository.save(history);
        }

        // 4. Cập nhật các thông tin trên đơn hàng
        order.setTrangThai(newStatus);
        order.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        order.setTrangThaiThanhToan(request.getTrangThaiThanhToan());
        order.setNgayCapNhat(LocalDateTime.now());

        // 5. Lưu lại đơn hàng đã cập nhật
        orderRepository.save(order);
    }
}
