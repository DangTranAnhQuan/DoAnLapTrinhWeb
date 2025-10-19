package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByTenSanPhamIgnoreCase(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCaseAndMaSanPhamNot(String tenSanPham, Integer productId);
    boolean existsByDanhMuc_MaDanhMuc(Integer categoryId);
    boolean existsByThuongHieu_MaThuongHieu(Integer brandId);

//    User
    @Query("SELECT p FROM Product p WHERE p.kichHoat = true AND (" +
            "LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.danhMuc.tenDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.thuongHieu.tenThuongHieu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchForUser(@Param("keyword") String keyword, Pageable pageable);
}
