package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Cart;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.repository.CartRepository;
import nhom17.OneShop.repository.UserRepository;
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
    private CartRepository gioHangRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository nguoiDungRepository;

    @Override
    @Transactional
    public void addToCart(Integer productId, int quantity) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));

        if (product.getInventory() == null || product.getInventory().getSoLuongTon() <= 0) {
            throw new RuntimeException("Sản phẩm đã hết hàng.");
        }

        Optional<Cart> existingCartItem = gioHangRepository.findByNguoiDungAndSanPham(currentUser, product);

        if (existingCartItem.isPresent()) {
            Cart cartItem = existingCartItem.get();
            cartItem.setSoLuong(cartItem.getSoLuong() + quantity);
            gioHangRepository.save(cartItem);
        } else {
            Cart newCartItem = new Cart();
            newCartItem.setNguoiDung(currentUser);
            newCartItem.setSanPham(product);
            newCartItem.setSoLuong(quantity);
            newCartItem.setDonGia(product.getGiaBan());
            gioHangRepository.save(newCartItem);
        }
    }

    @Override
    public List<Cart> getCartItems() {
        User currentUser = getCurrentUser();
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
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));
        Cart cartItem = gioHangRepository.findByNguoiDungAndSanPham(currentUser, product)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng."));
        cartItem.setSoLuong(quantity);
        gioHangRepository.save(cartItem);
    }

    @Override
    @Transactional
    public void removeItem(Integer productId) {
        User currentUser = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));
        gioHangRepository.deleteByNguoiDungAndSanPham(currentUser, product);
    }

    private User getCurrentUser() {
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