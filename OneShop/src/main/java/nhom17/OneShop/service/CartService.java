package nhom17.OneShop.service;

import nhom17.OneShop.entity.Cart;
import java.util.List;

public interface CartService {
    void addToCart(Integer productId, int quantity);
    List<Cart> getCartItems();
    void updateQuantity(Integer productId, int quantity);
    void removeItem(Integer productId);
}