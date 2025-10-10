package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.*;
import nhom17.OneShop.repository.OrderRepository;
import nhom17.OneShop.repository.OrderStatusHistoryRepository;
import nhom17.OneShop.repository.ShippingCarrierRepository;
import nhom17.OneShop.repository.ShippingRepository;
import nhom17.OneShop.request.ShippingRequest;
import nhom17.OneShop.service.ShippingService;
import nhom17.OneShop.specification.ShippingSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingRepository shippingRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ShippingCarrierRepository shippingCarrierRepository;
    @Autowired // ✅ THÊM VÀO
    private OrderStatusHistoryRepository historyRepository;

    @Override
    public Page<Shipping> search(String keyword, Integer carrierId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("guiLuc").descending());
        Specification<Shipping> spec = ShippingSpecification.filterBy(keyword, carrierId, status);
        return shippingRepository.findAll(spec, pageable);
    }

    @Override
    public Shipping findById(Long id) {
        return shippingRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(ShippingRequest request) {
        Order order = orderRepository.findById(request.getMaDonHang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với mã: " + request.getMaDonHang()));

        ShippingCarrier carrier = shippingCarrierRepository.findById(request.getMaNVC())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà vận chuyển!"));

        Shipping shipping;
        String newShippingStatus = request.getTrangThai();
        String oldOrderStatus = order.getTrangThai();

        // Chế độ SỬA
        if (request.getMaVanChuyen() != null) {
            shipping = shippingRepository.findById(request.getMaVanChuyen())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vận chuyển!"));

            shipping.setNhaVanChuyen(carrier);
            shipping.setTrangThai(newShippingStatus);

            if ("Đã giao".equals(newShippingStatus) && shipping.getGiaoLuc() == null) {
                shipping.setGiaoLuc(LocalDateTime.now());
            }

            // Chế độ THÊM MỚI
        } else {
            // ✅ KIỂM TRA QUAN TRỌNG: Đơn hàng này đã có đơn vận chuyển chưa?
            if (shippingRepository.existsByDonHang_MaDonHang(request.getMaDonHang())) {
                throw new RuntimeException("Đơn hàng #" + request.getMaDonHang() + " đã có đơn vận chuyển. Không thể tạo thêm.");
            }

            shipping = new Shipping();
            shipping.setDonHang(order);

            String randomCode = UUID.randomUUID().toString().substring(0, 10).toUpperCase();
            shipping.setMaVanDon(randomCode);

            shipping.setTrangThai("Đã khởi tạo");
            shipping.setGuiLuc(LocalDateTime.now());
            shipping.setNhaVanChuyen(carrier);

            newShippingStatus = "Đã khởi tạo";
        }

        // Logic cập nhật trạng thái đơn hàng (giữ nguyên)
        String newOrderStatus = null;
        switch (newShippingStatus) {
            case "Đang giao":
                newOrderStatus = "Đang giao";
                break;
            case "Đã giao":
                newOrderStatus = "Đã giao";
                break;
            case "Trả hàng":
                newOrderStatus = "Trả hàng-Hoàn tiền";
                break;
        }

        // ✅ LOGIC MỚI: Nếu trạng thái đơn hàng có thay đổi, thì tạo lịch sử
        if (newOrderStatus != null && !Objects.equals(oldOrderStatus, newOrderStatus)) {
            // Cập nhật trạng thái mới cho đơn hàng
            order.setTrangThai(newOrderStatus);

            // Tạo một bản ghi lịch sử
            OrderStatusHistory history = new OrderStatusHistory();
            history.setDonHang(order);
            history.setTuTrangThai(oldOrderStatus);
            history.setDenTrangThai(newOrderStatus);
            history.setThoiDiemThayDoi(LocalDateTime.now());

            // Tạm gán người thực hiện là admin (bạn có thể thay đổi sau khi có hệ thống đăng nhập)
            User adminUser = new User();
            adminUser.setMaNguoiDung(1);
            history.setNguoiThucHien(adminUser);

            // Lưu lịch sử và đơn hàng
            historyRepository.save(history);
            orderRepository.save(order);
        }

        // Cuối cùng, lưu đơn vận chuyển
        shippingRepository.save(shipping);
    }

    @Override
    public void delete(Long id) {
        // Cân nhắc: Có thể bạn muốn thêm logic cập nhật lại trạng thái đơn hàng khi xóa
        shippingRepository.deleteById(id);
    }
}
