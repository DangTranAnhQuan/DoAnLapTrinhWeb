package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.*;
import nhom17.OneShop.repository.*;
import nhom17.OneShop.dto.DashboardDataDTO;
import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.OrderStatusHistory;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.exception.NotFoundException;
import nhom17.OneShop.repository.OrderRepository;
import nhom17.OneShop.repository.OrderStatusHistoryRepository;
import nhom17.OneShop.repository.RatingRepository;
import nhom17.OneShop.request.OrderUpdateRequest;
import nhom17.OneShop.dto.TopSellingProductDTO;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.OrderService;
import nhom17.OneShop.specification.OrderSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nhom17.OneShop.entity.Inventory;
import nhom17.OneShop.entity.OrderDetail;
import nhom17.OneShop.repository.InventoryRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderStatusHistoryRepository historyRepository;

    @Autowired
    ShippingFeeRepository shippingFeeRepository;

    @Autowired
    MembershipTierRepository membershipTierRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Page<Order> findAll(String keyword, String status, String paymentMethod, String paymentStatus, String shippingMethod, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayDat").descending());

        return orderRepository.findAll(
                OrderSpecification.filterOrders(keyword, status, paymentMethod, paymentStatus, shippingMethod),
                pageable
        );
    }

    @Override
    public Order findById(long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Map<Long, List<ShippingFee>> getCarriersWithFeesByOrder(List<Order> danhSachDonHang) {
        Map<Long, List<ShippingFee>> ketQua = new HashMap<>();

        for (Order donHang : danhSachDonHang) {
            String phuongThuc = donHang.getPhuongThucVanChuyen();
            String tinhThanh = null;

            if (donHang.getDiaChi() != null && donHang.getDiaChi().getTinhThanh() != null) {
                tinhThanh = donHang.getDiaChi().getTinhThanh().trim();
            }

            // Nếu thiếu thông tin, bỏ qua
            if (phuongThuc == null || tinhThanh == null) {
                ketQua.put(donHang.getMaDonHang(), Collections.emptyList());
                continue;
            }

            // Lấy danh sách phí theo phương thức & tỉnh thành
            List<ShippingFee> danhSachPhi = shippingFeeRepository
                    .findByPhuongThucVanChuyenIgnoreCaseAndCacTinhApDung_Id_TenTinhThanh(phuongThuc, tinhThanh);

            // Loại bỏ trùng lặp theo nhà vận chuyển (nếu có)
            List<ShippingFee> danhSachPhiKhongTrung = new ArrayList<>();
            Set<Integer> maNhaVanChuyenDaCo = new HashSet<>();

            for (ShippingFee phi : danhSachPhi) {
                ShippingCarrier nhaVanChuyen = phi.getNhaVanChuyen();
                if (nhaVanChuyen != null && maNhaVanChuyenDaCo.add(nhaVanChuyen.getMaNVC())) {
                    danhSachPhiKhongTrung.add(phi);
                }
            }

            ketQua.put(donHang.getMaDonHang(), danhSachPhiKhongTrung);
        }
        return ketQua;
    }



    @Override
    @Transactional
    public void update(Long orderId, OrderUpdateRequest request) {
        Order order = findById(orderId);
        String oldStatus = order.getTrangThai();
        String newStatus = request.getTrangThai();

        if (!Objects.equals(oldStatus, newStatus)) {
            User currentUser = getCurrentUser();
            logStatusChange(order, oldStatus, newStatus, currentUser);
        }

        updateLoyaltyPoints(order, oldStatus, newStatus);

        order.setTrangThai(newStatus);
        order.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        order.setTrangThaiThanhToan(request.getTrangThaiThanhToan());

        orderRepository.save(order);
    }
    @Override
    @Transactional
    public void cancelOrder(Long orderId, User currentUser) {
        // 1. Tìm đơn hàng và kiểm tra tồn tại
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy đơn hàng #" + orderId));

        // 2. Kiểm tra bảo mật: Đơn hàng phải thuộc về người dùng hiện tại
        if (!order.getNguoiDung().getMaNguoiDung().equals(currentUser.getMaNguoiDung())) {
            throw new AccessDeniedException("Bạn không có quyền hủy đơn hàng này.");
        }

        // 3. Kiểm tra logic nghiệp vụ: Chỉ hủy khi ở trạng thái "Đang xử lý"
        if (!"Đang xử lý".equals(order.getTrangThai())) {
            throw new IllegalStateException("Chỉ có thể hủy đơn hàng khi ở trạng thái 'Đang xử lý'.");
        }

        // 4. Hoàn trả số lượng sản phẩm về kho
        for (OrderDetail detail : order.getOrderDetails()) {
            Inventory inventory = inventoryRepository.findBySanPham(detail.getSanPham())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setSanPham(detail.getSanPham());
                        newInventory.setSoLuongTon(0);
                        return newInventory;
                    });

            inventory.setSoLuongTon(inventory.getSoLuongTon() + detail.getSoLuong());
            inventoryRepository.save(inventory);
        }

        // 5. Cập nhật trạng thái đơn hàng và lưu lại
        order.setTrangThai("Đã hủy");

        orderRepository.save(order);
    }

    private void logStatusChange(Order order, String oldStatus, String newStatus, User adminUser) {
        OrderStatusHistory history = new OrderStatusHistory();
        history.setDonHang(order);
        history.setTuTrangThai(oldStatus);
        history.setDenTrangThai(newStatus);
        history.setThoiDiemThayDoi(LocalDateTime.now());
        history.setNguoiThucHien(adminUser);
        historyRepository.save(history);
    }

    @Override
    @Transactional
    public void updateLoyaltyPoints(Order order, String oldStatus, String newStatus) {
        User user = order.getNguoiDung();
        if (user == null || user.getMaNguoiDung() == null) {
            return;
        }

        int points = calculatePointsFromOrder(order);
        if (points == 0) {
            return;
        }

        if (!"Đã giao".equals(oldStatus) && "Đã giao".equals(newStatus)) {
            adjustUserPoints(user, points);
        }

        else if ("Đã giao".equals(oldStatus) && "Trả hàng-Hoàn tiền".equals(newStatus)) {
            adjustUserPoints(user, -points);
        }
    }

    private int calculatePointsFromOrder(Order order) {
        BigDecimal tienHang = order.getTienHang(); //
        if (tienHang == null || tienHang.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return tienHang.divide(new BigDecimal("10000"), 0, RoundingMode.FLOOR).intValue();
    }

    private void adjustUserPoints(User user, int pointsToAdjust) {
        User userToUpdate = nguoiDungRepository.findById(user.getMaNguoiDung()).orElse(null);
        if (userToUpdate == null) return;

        int newTotalPoints = userToUpdate.getDiemTichLuy() + pointsToAdjust;
        userToUpdate.setDiemTichLuy(Math.max(0, newTotalPoints));

        capNhatHangThanhVien(userToUpdate);

        nguoiDungRepository.save(userToUpdate);
    }

    private void capNhatHangThanhVien(User user) {
        int currentPoints = user.getDiemTichLuy();

        List<MembershipTier> eligibleTiers = membershipTierRepository.findByDiemToiThieuLessThanEqualOrderByDiemToiThieuDesc(currentPoints);

        if (!eligibleTiers.isEmpty()) {
            MembershipTier newTier = eligibleTiers.get(0);
            if (user.getHangThanhVien() == null || !newTier.getMaHangThanhVien().equals(user.getHangThanhVien().getMaHangThanhVien())) {
                user.setHangThanhVien(newTier);
            }
        }
    }

    @Override
    public DashboardDataDTO getDashboardData(int year, int month) {
        DashboardDataDTO data = new DashboardDataDTO();
        YearMonth yearMonth = YearMonth.of(year, month);

        Timestamp startOfMonth = Timestamp.valueOf(yearMonth.atDay(1).atStartOfDay());
        Timestamp endOfMonth = Timestamp.valueOf(yearMonth.atEndOfMonth().plusDays(1).atStartOfDay());

        // 1. Lấy KPI Doanh thu và Tổng đơn hàng
        List<Object[]> kpiResults = orderRepository.findKpiDataBetween(startOfMonth, endOfMonth);
        if (!kpiResults.isEmpty() && kpiResults.get(0) != null) {
            Object[] kpiData = kpiResults.get(0);
            if (kpiData[0] != null) data.setTotalRevenue(new BigDecimal(kpiData[0].toString()));
            if (kpiData[1] != null) data.setTotalOrders(Long.parseLong(kpiData[1].toString()));
        }

        // 2. Lấy KPI Sản phẩm đã bán và Giá vốn
        List<Object[]> productsAndCogsResults = orderRepository.findProductsAndCogsDataBetween(startOfMonth, endOfMonth);
        if (!productsAndCogsResults.isEmpty() && productsAndCogsResults.get(0) != null) {
            Object[] cogsData = productsAndCogsResults.get(0);
            if (cogsData[0] != null) data.setTotalProductsSold(Long.parseLong(cogsData[0].toString()));
            if (cogsData[1] != null) data.setTotalCostOfGoodsSold(new BigDecimal(cogsData[1].toString()));
        }

        // 3. Tính toán các KPI còn lại
        data.setTotalProfit(data.getTotalRevenue().subtract(data.getTotalCostOfGoodsSold()));
        if (data.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
            double margin = data.getTotalProfit().doubleValue() / data.getTotalRevenue().doubleValue() * 100;
            data.setProfitMargin(margin);
        }
        data.setAverageOrderValue(data.getTotalOrders() > 0 ? data.getTotalRevenue().divide(BigDecimal.valueOf(data.getTotalOrders()), 0, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // 4. Dữ liệu biểu đồ doanh thu theo ngày
        List<Object[]> revenueByDayRaw = orderRepository.findRevenueByDayBetween(startOfMonth, endOfMonth);
        Map<Integer, BigDecimal> revenueMap = revenueByDayRaw.stream()
                .collect(Collectors.toMap(
                        row -> ((Date) row[0]).toLocalDate().getDayOfMonth(),
                        row -> new BigDecimal(row[1].toString())
                ));
        List<String> dayLabels = new ArrayList<>();
        List<BigDecimal> dayChartData = new ArrayList<>();
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            dayLabels.add("Ngày " + day);
            dayChartData.add(revenueMap.getOrDefault(day, BigDecimal.ZERO));
        }
        data.setRevenueByDayLabels(dayLabels);
        data.setRevenueByDayData(dayChartData);

        // 5. Dữ liệu Top sản phẩm
        List<Object[]> topProductsRaw = orderRepository.findTopSellingProductsBetween(startOfMonth, endOfMonth, 5);
        data.setTopSellingProducts(topProductsRaw.stream()
                .map(row -> new TopSellingProductDTO((String) row[0], (String) row[1], ((Number) row[2]).longValue(), new BigDecimal(row[3].toString())))
                .collect(Collectors.toList()));

        // 6. Dữ liệu biểu đồ doanh thu theo danh mục
        List<Object[]> revenueByCategoryRaw = orderRepository.findRevenueByCategoryBetween(startOfMonth, endOfMonth);
        data.setRevenueByCategoryLabels(revenueByCategoryRaw.stream().map(row -> (String) row[0]).collect(Collectors.toList()));
        data.setRevenueByCategoryData(revenueByCategoryRaw.stream().map(row -> new BigDecimal(row[1].toString())).collect(Collectors.toList()));

        return data;
    }

//    User
    @Autowired private UserRepository nguoiDungRepository;

    @Override
    public List<Order> findOrdersForCurrentUser() {
        User currentUser = getCurrentUser();
        return orderRepository.findByNguoiDungOrderByNgayDatDesc(currentUser);
    }

    @Override
    public Order findOrderByIdForCurrentUser(Long orderId) {
        User currentUser = getCurrentUser();
        Order order = orderRepository.findByIdWithDetails(orderId)
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

    @Override
    public boolean hasCompletedPurchase(Integer userId, Integer productId) {
        return orderRepository.hasCompletedPurchase(userId, productId);
    }

    @Override
    public boolean canUserReviewProduct(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return false;
        }
        // Kiểm tra xem đã mua và đơn hàng đã hoàn thành chưa
        boolean hasPurchased = orderRepository.hasCompletedPurchase(userId, productId);
        if (!hasPurchased) {
            return false;
        }
        // Kiểm tra xem đã đánh giá sản phẩm này bao giờ chưa
        boolean hasReviewed = ratingRepository.existsByNguoiDung_MaNguoiDungAndSanPham_MaSanPham(userId, productId);
        return !hasReviewed;
    }

    @Override
    @Transactional
    public void processSepayPayment(Long orderId, BigDecimal amountPaid) {
        // 1. Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Webhook: Không tìm thấy đơn hàng #" + orderId));

        // 2. Kiểm tra nếu đã thanh toán rồi thì bỏ qua
        if ("Đã thanh toán".equalsIgnoreCase(order.getTrangThaiThanhToan())) {
            System.out.println("Webhook: Đơn hàng #" + orderId + " đã được thanh toán trước đó. Bỏ qua.");
            return; // Đã xử lý rồi
        }

        // 3. Kiểm tra số tiền (rất quan trọng)
        // Dùng compareTo() cho BigDecimal
        if (order.getTongTien().compareTo(amountPaid) != 0) {
            throw new IllegalStateException("Webhook: Số tiền thanh toán không khớp. Đơn hàng "
                    + order.getTongTien() + " | Đã trả " + amountPaid);
        }

        // 4. Kiểm tra trạng thái
        if (!"Chưa thanh toán".equalsIgnoreCase(order.getTrangThaiThanhToan())) {
            throw new IllegalStateException("Webhook: Đơn hàng không ở trạng thái 'Chưa thanh toán'.");
        }

        // 5. Cập nhật trạng thái
        String oldStatus = order.getTrangThai();
        order.setTrangThaiThanhToan("Đã thanh toán");

        // Nếu đơn hàng đang là "Đang xử lý" thì có thể chuyển thành "Đã xác nhận"
        if("Đang xử lý".equals(oldStatus) || "Chưa thanh toán".equals(oldStatus)) {
            order.setTrangThai("Đã thanh toán");
        }

        orderRepository.save(order);
        System.out.println("Webhook: Đã cập nhật thanh toán thành công cho đơn hàng #" + orderId);

        try {
            User orderUser = order.getNguoiDung();
            if (orderUser != null) {
                cartService.clearCart();

                System.out.println("Webhook: Đã xóa giỏ hàng cho người dùng của đơn hàng #" + orderId);

            } else {
                System.err.println("Webhook Warning: Không tìm thấy người dùng cho đơn hàng #" + orderId + " để xóa giỏ hàng.");
            }
        } catch (Exception e) {
            System.err.println("Webhook Error: Lỗi khi xóa giỏ hàng cho đơn hàng #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

}
