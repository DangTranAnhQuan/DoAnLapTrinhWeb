package nhom17.OneShop.repository;

import nhom17.OneShop.entity.ThuongHieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<ThuongHieu, Integer> {
    Page<ThuongHieu> findAll(Pageable pageable);
    Page<ThuongHieu> findAllByTenThuongHieuContaining(String keyword, Pageable pageable);
    Page<ThuongHieu> findAllByKichHoat(boolean status, Pageable pageable);
    Page<ThuongHieu> findAllByTenThuongHieuContainingAndKichHoat(String keyword, boolean status, Pageable pageable);
}
