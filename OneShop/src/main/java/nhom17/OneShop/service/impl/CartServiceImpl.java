package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.GioHang;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.repository.GioHangRepository;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional
    public void addToCart(Integer productId, int quantity) {
        NguoiDung currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));
        Optional<GioHang> existingCartItem = gioHangRepository.findByNguoiDungAndSanPham(currentUser, product);

        if (existingCartItem.isPresent()) {
            GioHang cartItem = existingCartItem.get();
            cartItem.setSoLuong(cartItem.getSoLuong() + quantity);
            gioHangRepository.save(cartItem);
        } else {
            GioHang newCartItem = new GioHang();
            newCartItem.setNguoiDung(currentUser);
            newCartItem.setSanPham(product);
            newCartItem.setSoLuong(quantity);
            newCartItem.setDonGia(product.getGiaBan());
            gioHangRepository.save(newCartItem);
        }
    }

    @Override
    public List<GioHang> getCartItems() {
        NguoiDung currentUser = getCurrentUser();
        // ✅ GỌI ĐẾN PHƯƠNG THỨC MỚI VỚI JOIN FETCH
        return gioHangRepository.findByNguoiDungWithProduct(currentUser);
    }

    @Override
    @Transactional
    public void updateQuantity(Integer productId, int quantity) {
        if (quantity < 1) {
            removeItem(productId);
            return;
        }
        NguoiDung currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));
        GioHang cartItem = gioHangRepository.findByNguoiDungAndSanPham(currentUser, product)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng."));
        cartItem.setSoLuong(quantity);
        gioHangRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeItem(Integer productId) {
        NguoiDung currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));
        gioHangRepository.deleteByNguoiDungAndSanPham(currentUser, product);
    }

    private NguoiDung getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            throw new IllegalStateException("Người dùng chưa đăng nhập.");
        }
        return nguoiDungRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng trong CSDL: " + username));
    }
}