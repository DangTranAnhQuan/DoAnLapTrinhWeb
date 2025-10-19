package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Rating;
import nhom17.OneShop.entity.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingId> {
    List<Rating> findBySanPham_MaSanPhamOrderByNgayTaoDesc(Integer maSanPham);
}
