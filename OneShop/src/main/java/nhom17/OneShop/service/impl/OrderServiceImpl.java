package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.DonHang;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.repository.DonHangRepository;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private DonHangRepository donHangRepository;
    @Autowired private NguoiDungRepository nguoiDungRepository;

    @Override
    public List<DonHang> findOrdersForCurrentUser() {
        NguoiDung currentUser = getCurrentUser();
        return donHangRepository.findByNguoiDungOrderByNgayDatDesc(currentUser);
    }

    @Override
    public DonHang findOrderByIdForCurrentUser(Long orderId) {
        NguoiDung currentUser = getCurrentUser();
        DonHang order = donHangRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng."));

        // Kiểm tra bảo mật: đảm bảo đơn hàng này thuộc về người dùng đang đăng nhập
        if (!order.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
            throw new AccessDeniedException("Bạn không có quyền xem đơn hàng này.");
        }
        return order;
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username).orElseThrow();
    }
}