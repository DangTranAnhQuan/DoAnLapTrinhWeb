package nhom17.OneShop.service;

import nhom17.OneShop.entity.GioHang;
import java.util.List;

public interface CartService {
    void addToCart(Integer productId, int quantity);
    List<GioHang> getCartItems();
    void updateQuantity(Integer productId, int quantity);
    void removeItem(Integer productId);
}