package nhom17.OneShop.service.impl;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.*;
import nhom17.OneShop.repository.*;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired private CartService cartService;
    @Autowired private NguoiDungRepository nguoiDungRepository;
    @Autowired private DiaChiRepository diaChiRepository;
    @Autowired private DonHangRepository donHangRepository;
    @Autowired private DonHangChiTietRepository donHangChiTietRepository;
    @Autowired private GioHangRepository gioHangRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private KhuyenMaiRepository khuyenMaiRepository; // Thêm repo khuyến mãi
    @Autowired private HttpSession session; // Thêm HttpSession để đọc dữ liệu session

    @Override
    @Transactional
    public void placeOrder(Integer diaChiId, String paymentMethod) {
        NguoiDung currentUser = getCurrentUser();
        List<GioHang> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống.");
        }

        DiaChi shippingAddress = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không hợp lệ."));

        // Bắt đầu tạo đơn hàng
        DonHang order = new DonHang();
        order.setNguoiDung(currentUser);
        order.setNgayDat(LocalDateTime.now());
        order.setTrangThai("Đang xử lý");
        order.setPhuongThucThanhToan(paymentMethod);
        order.setTrangThaiThanhToan("Chưa thanh toán");

        order.setNgayTao(LocalDateTime.now());

        BigDecimal subtotal = cartItems.stream().map(GioHang::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTienHang(subtotal);
        order.setPhiVanChuyen(BigDecimal.ZERO); // Tạm thời phí ship là 0

        // Lấy giảm giá từ coupon trong session
        BigDecimal couponDiscount = (BigDecimal) session.getAttribute("cartDiscount");
        if (couponDiscount == null) {
            couponDiscount = BigDecimal.ZERO;
        }

        // Lấy mã coupon và gán vào đơn hàng
        String appliedCouponCode = (String) session.getAttribute("appliedCouponCode");
        if (appliedCouponCode != null) {
            KhuyenMai km = khuyenMaiRepository.findById(appliedCouponCode).orElse(null);
            order.setKhuyenMai(km);
        }

        // Lấy giảm giá từ hạng thành viên
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        if (currentUser.getHangThanhVien() != null) {
            BigDecimal discountPercent = currentUser.getHangThanhVien().getPhanTramGiamGia();
            if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
                membershipDiscount = subtotal.multiply(discountPercent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            }
        }

        // Tính tổng tiền cuối cùng
        BigDecimal total = subtotal.subtract(couponDiscount).subtract(membershipDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
        order.setTongTien(total);

        // Sao chép thông tin giao hàng
        order.setTenNguoiNhan(shippingAddress.getTenNguoiNhan());
        order.setSoDienThoaiNhan(shippingAddress.getSoDienThoai());
        String fullAddress = String.join(", ", shippingAddress.getSoNhaDuong(), shippingAddress.getPhuongXa(), shippingAddress.getQuanHuyen(), shippingAddress.getTinhThanh());
        order.setDiaChiNhan(fullAddress);

        DonHang savedOrder = donHangRepository.save(order);

        // 2. Chuyển sản phẩm từ giỏ hàng sang chi tiết đơn hàng
        for (GioHang cartItem : cartItems) {
            DonHang_ChiTiet orderDetail = new DonHang_ChiTiet();
            orderDetail.setDonHang(savedOrder);
            orderDetail.setSanPham(cartItem.getSanPham());
            orderDetail.setSoLuong(cartItem.getSoLuong());
            orderDetail.setDonGia(cartItem.getDonGia());
            orderDetail.setTenSanPham(cartItem.getSanPham().getTenSanPham());
            donHangChiTietRepository.save(orderDetail);

            // 3. Cập nhật tồn kho
            Inventory inventory = inventoryRepository.findById(cartItem.getSanPham().getMaSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong kho."));
            inventory.setSoLuongTon(inventory.getSoLuongTon() - cartItem.getSoLuong());
            inventoryRepository.save(inventory);
        }

        // 4. Xóa giỏ hàng
        gioHangRepository.deleteAll(cartItems);
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
    }
}