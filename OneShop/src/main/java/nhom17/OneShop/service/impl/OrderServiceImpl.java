package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.OrderRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository donHangRepository;
    @Autowired private UserRepository nguoiDungRepository;

    @Override
    public List<Order> findOrdersForCurrentUser() {
        User currentUser = getCurrentUser();
        return donHangRepository.findByNguoiDungOrderByNgayDatDesc(currentUser);
    }

    @Override
    public Order findOrderByIdForCurrentUser(Long orderId) {
        User currentUser = getCurrentUser();
        Order order = donHangRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng."));

        // Kiểm tra bảo mật: đảm bảo đơn hàng này thuộc về người dùng đang đăng nhập
        if (!order.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này.");
        }
        return order;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}