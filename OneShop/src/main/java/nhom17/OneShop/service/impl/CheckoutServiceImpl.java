package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.*;
import nhom17.OneShop.repository.*;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.CheckoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired private CartService cartService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private HttpSession httpSession;
    @Autowired private VoucherRepository voucherRepository;


    @Override
    @Transactional
    public Order placeOrder(Integer diaChiId, String paymentMethod, BigDecimal shippingFee, String shippingMethodName) {
        User currentUser = getCurrentUserOptional().orElseThrow(() -> new IllegalStateException("Người dùng chưa đăng nhập."));
        List<Cart> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng đang trống.");
        }
        Address shippingAddress = addressRepository.findById(diaChiId)
                .filter(addr -> Objects.equals(addr.getNguoiDung().getMaNguoiDung(), currentUser.getMaNguoiDung()))
                .orElseThrow(() -> new RuntimeException("Địa chỉ giao hàng không hợp lệ."));

        BigDecimal subtotal = cartService.getSubtotal();
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        Optional<User> userWithTierOpt = userRepository.findByEmailWithMembership(currentUser.getEmail());
        if (userWithTierOpt.isPresent() && userWithTierOpt.get().getHangThanhVien() != null) {
            BigDecimal percent = userWithTierOpt.get().getHangThanhVien().getPhanTramGiamGia();
            if (percent != null && percent.compareTo(BigDecimal.ZERO) > 0) {
                membershipDiscount = subtotal.multiply(percent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            }
        }
        BigDecimal priceAfterMembership = subtotal.subtract(membershipDiscount).max(BigDecimal.ZERO);

        BigDecimal actualCouponDiscount = BigDecimal.ZERO;
        String appliedCouponCode = (String) httpSession.getAttribute("appliedCouponCode");
        Voucher appliedVoucher = null; // Khởi tạo voucher áp dụng là null

        // Biến để lưu thông báo lỗi nếu có
        String voucherErrorMessage = null;

        if (appliedCouponCode != null) {
            Optional<Voucher> voucherOpt = voucherRepository.findByMaKhuyenMaiAndTrangThai(appliedCouponCode, 1); // Tìm voucher đang active

            // 1. Kiểm tra cơ bản (Tồn tại, còn hạn, đủ tiền tối thiểu)
            if (voucherOpt.isPresent()
                    && voucherOpt.get().getBatDauLuc().isBefore(LocalDateTime.now())
                    && voucherOpt.get().getKetThucLuc().isAfter(LocalDateTime.now())
                    && (voucherOpt.get().getTongTienToiThieu() == null || priceAfterMembership.compareTo(voucherOpt.get().getTongTienToiThieu()) >= 0))
            {
                Voucher voucher = voucherOpt.get(); // Lấy voucher ra

                // ==== BẮT ĐẦU THÊM KIỂM TRA GIỚI HẠN ====
                boolean limitsOk = true;
                List<String> invalidOrderStatesForUsageCount = List.of("Đã hủy", "Đang xử lý");

                // 2. Kiểm tra giới hạn tổng số lượt sử dụng
                Integer totalLimit = voucher.getGioiHanTongSoLan();
                if (totalLimit != null && totalLimit > 0) {
                    // Gọi hàm count mới với danh sách trạng thái không hợp lệ
                    long totalUses = orderRepository.countByKhuyenMai_MaKhuyenMaiAndTrangThaiNotIn(voucher.getMaKhuyenMai(), invalidOrderStatesForUsageCount);
                    if (totalUses >= totalLimit) {
                        voucherErrorMessage = "Mã khuyến mãi '" + voucher.getMaKhuyenMai() + "' đã hết lượt sử dụng.";
                        limitsOk = false;
                    }
                }

                // 3. Kiểm tra giới hạn mỗi người
                Integer userLimit = voucher.getGioiHanMoiNguoi();
                if (limitsOk && userLimit != null && userLimit > 0) {
                    // Gọi hàm count mới với danh sách trạng thái không hợp lệ
                    long userUses = orderRepository.countByNguoiDungAndKhuyenMai_MaKhuyenMaiAndTrangThaiNotIn(currentUser, voucher.getMaKhuyenMai(), invalidOrderStatesForUsageCount);
                    if (userUses >= userLimit) {
                        voucherErrorMessage = "Bạn đã hết lượt sử dụng mã khuyến mãi '" + voucher.getMaKhuyenMai() + "'.";
                        limitsOk = false;
                    }
                }
                // ==== KẾT THÚC THÊM KIỂM TRA GIỚI HẠN ====

                // 4. Nếu tất cả đều OK -> Tính giảm giá
                if (limitsOk) {
                    appliedVoucher = voucher; // Gán voucher hợp lệ
                    BigDecimal discountValue = appliedVoucher.getGiaTri();
                    // Tính giá trị giảm thực tế
                    if (appliedVoucher.getKieuApDung() != null && appliedVoucher.getKieuApDung() == 0) { // Percentage
                        actualCouponDiscount = priceAfterMembership.multiply(discountValue.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                        if (appliedVoucher.getGiamToiDa() != null && actualCouponDiscount.compareTo(appliedVoucher.getGiamToiDa()) > 0) {
                            actualCouponDiscount = appliedVoucher.getGiamToiDa();
                        }
                    } else { // Fixed amount
                        actualCouponDiscount = discountValue;
                    }
                    actualCouponDiscount = actualCouponDiscount.min(priceAfterMembership); // Đảm bảo không âm
                }
                // Nếu không OK (limitsOk == false), không làm gì cả, appliedVoucher vẫn là null, actualCouponDiscount là 0

            } else { // Nếu voucher không hợp lệ ngay từ đầu (hết hạn, ko đủ tiền...)
                if (voucherOpt.isPresent()) { // Chỉ đặt thông báo lỗi nếu voucher tồn tại nhưng ko hợp lệ
                    voucherErrorMessage = "Mã khuyến mãi '" + appliedCouponCode + "' không hợp lệ hoặc không đủ điều kiện.";
                } else {
                    voucherErrorMessage = "Không tìm thấy mã khuyến mãi '" + appliedCouponCode + "'.";
                }
            }

            // 5. Nếu có lỗi (không hợp lệ HOẶC hết lượt) -> Xóa khỏi session
            if (appliedVoucher == null) { // appliedVoucher chỉ được gán nếu mọi thứ OK
                httpSession.removeAttribute("cartDiscount");
                httpSession.removeAttribute("appliedCouponCode");
                actualCouponDiscount = BigDecimal.ZERO; // Đảm bảo không có giảm giá

                // QUAN TRỌNG: Ném Exception để báo lỗi cho người dùng biết tại sao mã bị gỡ
                if (voucherErrorMessage != null) {
                    throw new IllegalStateException(voucherErrorMessage);
                } else if(appliedCouponCode != null) {
                    // Trường hợp lỗi không xác định
                    throw new IllegalStateException("Mã khuyến mãi '" + appliedCouponCode + "' không thể áp dụng.");
                }
            }
        }
        BigDecimal finalTotal = priceAfterMembership.subtract(actualCouponDiscount).add(shippingFee).max(BigDecimal.ZERO);

        // Create Order
        Order order = new Order();
        order.setNguoiDung(currentUser);
        order.setNgayDat(LocalDateTime.now());
        order.setTrangThai("Đang xử lý");
        order.setPhuongThucThanhToan(paymentMethod);
        order.setTrangThaiThanhToan("Chưa thanh toán");
        order.setTienHang(subtotal);

        order.setKhuyenMai(appliedVoucher);

        order.setPhiVanChuyen(shippingFee);
        order.setPhuongThucVanChuyen(shippingMethodName);
        order.setTongTien(finalTotal);
        order.setTenNguoiNhan(shippingAddress.getTenNguoiNhan());
        order.setSoDienThoaiNhan(shippingAddress.getSoDienThoai());
        String fullAddress = String.format("%s, %s, %s, %s",
                shippingAddress.getSoNhaDuong(), shippingAddress.getPhuongXa(),
                shippingAddress.getQuanHuyen(), shippingAddress.getTinhThanh());
        order.setDiaChiNhan(fullAddress);

        order.setDiaChi(shippingAddress);


        Order savedOrder = orderRepository.save(order);

        // Create Order Details and update Inventory
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            Product product = cartItem.getSanPham();
            int orderedQuantity = cartItem.getSoLuong();

            OrderDetail detail = new OrderDetail();
            detail.setDonHang(savedOrder);
            detail.setSanPham(product);
            detail.setTenSanPham(product.getTenSanPham());
            detail.setDonGia(cartItem.getDonGia());
            detail.setSoLuong(orderedQuantity);
            orderDetails.add(detail);

            Inventory inventory = inventoryRepository.findById(product.getMaSanPham())
                    .orElseThrow(() -> new RuntimeException("Hết hàng tồn kho cho sản phẩm: " + product.getTenSanPham()));
            if (inventory.getSoLuongTon() < orderedQuantity) {
                throw new IllegalStateException("Số lượng tồn kho không đủ cho sản phẩm: " + product.getTenSanPham());
            }
            inventory.setSoLuongTon(inventory.getSoLuongTon() - orderedQuantity);
            inventoryRepository.save(inventory);
        }

        orderDetailRepository.saveAll(orderDetails);

        if ("COD".equalsIgnoreCase(paymentMethod)) {
            cartService.clearCart(); // Xóa giỏ hàng cho người dùng hiện tại

            // Xóa thông tin giảm giá khỏi session (chỉ khi COD thành công)
            httpSession.removeAttribute("cartDiscount");
            httpSession.removeAttribute("appliedCouponCode");
            System.out.println("Giỏ hàng đã được xóa cho đơn hàng COD #" + savedOrder.getMaDonHang());
        } else {
            // Đối với thanh toán ONLINE, không xóa giỏ hàng ở đây.
            // Giỏ hàng sẽ được xóa sau khi Webhook xác nhận thanh toán thành công.
            System.out.println("Đơn hàng ONLINE #" + savedOrder.getMaDonHang() + " được tạo, giỏ hàng chưa xóa.");
        }

        return savedOrder;
    }

    // Helper method
    private Optional<User> getCurrentUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(username);
    }
}