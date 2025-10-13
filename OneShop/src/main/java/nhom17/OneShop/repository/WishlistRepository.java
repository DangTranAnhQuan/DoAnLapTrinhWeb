package nhom17.OneShop.repository;

import nhom17.OneShop.entity.User;
import nhom17.OneShop.entity.WishList;
import nhom17.OneShop.entity.WishListId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishList, WishListId> {
    List<WishList> findByNguoiDung(User nguoiDung);
    long countByNguoiDung(User nguoiDung);
    Optional<WishList> findByNguoiDungAndSanPham_MaSanPham(User nguoiDung, Integer productId);
}