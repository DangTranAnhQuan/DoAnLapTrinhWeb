package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByTenSanPhamIgnoreCase(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCaseAndMaSanPhamNot(String tenSanPham, Integer productId);
    boolean existsByDanhMuc_MaDanhMuc(Integer categoryId);
    boolean existsByThuongHieu_MaThuongHieu(Integer brandId);
}
