package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Integer>, JpaSpecificationExecutor<Brand> {
    Optional<Brand> findByTenThuongHieuIgnoreCase(String tenThuongHieu);
    boolean existsByTenThuongHieuIgnoreCase(String tenThuongHieu);
    boolean existsByTenThuongHieuIgnoreCaseAndMaThuongHieuNot(String tenThuongHieu, Integer brandId);
}
