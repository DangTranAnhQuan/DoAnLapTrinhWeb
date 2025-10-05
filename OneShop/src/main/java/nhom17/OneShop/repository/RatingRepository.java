package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Rating;
import nhom17.OneShop.entity.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    List<Rating> findBySanPham_MaSanPhamOrderByNgayTaoDesc(Integer maSanPham);
}
