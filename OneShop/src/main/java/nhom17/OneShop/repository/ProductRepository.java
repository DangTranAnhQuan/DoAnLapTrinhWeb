package nhom17.OneShop.repository;

import nhom17.OneShop.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<SanPham, Integer> {
    Page<SanPham> findByTenSanPhamContaining(String keyword, Pageable pageable);
    Page<SanPham> findByKichHoat(boolean status, Pageable pageable);
    Page<SanPham> findByTenSanPhamContainingAndKichHoat(String keyword, boolean status, Pageable pageable);
    Page<SanPham> findAll(Pageable pageable);
}
