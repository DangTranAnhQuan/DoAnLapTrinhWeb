package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.OrderStatusHistory;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.repository.OrderRepository;
import nhom17.OneShop.repository.OrderStatusHistoryRepository;
import nhom17.OneShop.request.DashboardDataDTO;
import nhom17.OneShop.request.OrderUpdateRequest;
import nhom17.OneShop.request.TopSellingProductDTO;
import nhom17.OneShop.repository.UserRepository;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public void update(Long orderId, OrderUpdateRequest request) {
        Order order = findById(orderId);
        String oldStatus = order.getTrangThai();
        String newStatus = request.getTrangThai();

        if (!Objects.equals(oldStatus, newStatus)) {
            User currentUser = getCurrentUser();
            logStatusChange(order, oldStatus, newStatus, currentUser);
        }

        order.setTrangThai(newStatus);
        order.setPhuongThucThanhToan(request.getPhuongThucThanhToan());
        order.setTrangThaiThanhToan(request.getTrangThaiThanhToan());
        order.setNgayCapNhat(LocalDateTime.now());

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
    public DashboardDataDTO getDashboardData(int year, int month) {
        DashboardDataDTO data = new DashboardDataDTO();
        YearMonth yearMonth = YearMonth.of(year, month);

        // ✅ THAY ĐỔI: Chuyển đổi sang Timestamp để truyền vào query
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
}
