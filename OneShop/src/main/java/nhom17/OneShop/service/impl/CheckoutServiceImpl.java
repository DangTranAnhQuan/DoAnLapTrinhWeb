package nhom17.OneShop.service.impl;

import jakarta.servlet.http.HttpSession;
import nhom17.OneShop.entity.*;
import nhom17.OneShop.exception.NotFoundException; // Thêm import
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

@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired private CartService cartService;
    @Autowired private UserRepository nguoiDungRepository;
    @Autowired private AddressRepository diaChiRepository;
    @Autowired private OrderRepository donHangRepository;
    @Autowired private OrderDetailRepository donHangChiTietRepository;
    @Autowired private CartRepository gioHangRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private VoucherRepository khuyenMaiRepository;
    @Autowired private HttpSession session;

    @Override
    @Transactional
    public void placeOrder(Integer diaChiId, String paymentMethod) {
        User currentUser = getCurrentUser();
        List<Cart> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống.");
        }

        for (Cart cartItem : cartItems) {
            Inventory inventory = inventoryRepository.findById(cartItem.getSanPham().getMaSanPham())
                    .orElseThrow(() -> new NotFoundException("Hệ thống không tìm thấy tồn kho cho sản phẩm: " + cartItem.getSanPham().getTenSanPham()));

            if (inventory.getSoLuongTon() < cartItem.getSoLuong()) {
                // Ném ra lỗi với thông báo rõ ràng, lỗi này sẽ được Controller bắt và hiển thị cho người dùng
                throw new IllegalStateException(
                        String.format("Sản phẩm '%s' không đủ số lượng. Bạn muốn mua %d, nhưng chỉ còn %d sản phẩm.",
                                cartItem.getSanPham().getTenSanPham(),
                                cartItem.getSoLuong(),
                                inventory.getSoLuongTon())
                );
            }
        }

        Address shippingAddress = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ không hợp lệ."));

        Order order = new Order();
        order.setNguoiDung(currentUser);
        order.setNgayDat(LocalDateTime.now());
        order.setTrangThai("Đang xử lý");
        order.setPhuongThucThanhToan(paymentMethod);
        order.setTrangThaiThanhToan("Chưa thanh toán");


        BigDecimal subtotal = cartItems.stream().map(Cart::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTienHang(subtotal);
        order.setPhiVanChuyen(BigDecimal.ZERO);

        BigDecimal couponDiscount = (BigDecimal) session.getAttribute("cartDiscount");
        if (couponDiscount == null) couponDiscount = BigDecimal.ZERO;

        String appliedCouponCode = (String) session.getAttribute("appliedCouponCode");
        if (appliedCouponCode != null) {
            khuyenMaiRepository.findById(appliedCouponCode).ifPresent(order::setKhuyenMai);
        }

        BigDecimal membershipDiscount = BigDecimal.ZERO;
        if (currentUser.getHangThanhVien() != null) {
            BigDecimal discountPercent = currentUser.getHangThanhVien().getPhanTramGiamGia();
            if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
                membershipDiscount = subtotal.multiply(discountPercent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            }
        }

        BigDecimal total = subtotal.subtract(couponDiscount).subtract(membershipDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) total = BigDecimal.ZERO;
        order.setTongTien(total);

        order.setTenNguoiNhan(shippingAddress.getTenNguoiNhan());
        order.setSoDienThoaiNhan(shippingAddress.getSoDienThoai());
        String fullAddress = String.join(", ", shippingAddress.getSoNhaDuong(), shippingAddress.getPhuongXa(), shippingAddress.getQuanHuyen(), shippingAddress.getTinhThanh());
        order.setDiaChiNhan(fullAddress);

        Order savedOrder = donHangRepository.save(order);

        for (Cart cartItem : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setDonHang(savedOrder);
            orderDetail.setSanPham(cartItem.getSanPham());
            orderDetail.setSoLuong(cartItem.getSoLuong());
            orderDetail.setDonGia(cartItem.getDonGia());
            orderDetail.setTenSanPham(cartItem.getSanPham().getTenSanPham());
            donHangChiTietRepository.save(orderDetail);

            Inventory inventory = inventoryRepository.findById(cartItem.getSanPham().getMaSanPham()).get();
            inventory.setSoLuongTon(inventory.getSoLuongTon() - cartItem.getSoLuong());
            inventoryRepository.save(inventory);
        }

        gioHangRepository.deleteAll(cartItems);

        // Xóa coupon khỏi session sau khi đặt hàng thành công
        session.removeAttribute("cartDiscount");
        session.removeAttribute("appliedCouponCode");
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return nguoiDungRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));
    }
}