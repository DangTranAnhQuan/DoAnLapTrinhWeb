package nhom17.OneShop.repository;

import nhom17.OneShop.entity.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<DanhMuc, Integer> {
    Page<DanhMuc> findAll(Pageable pageable);
    Page<DanhMuc> findAllByTenDanhMucContaining(String keyword, Pageable pageable);
    Page<DanhMuc> findAllByKichHoat(boolean status, Pageable pageable);
    Page<DanhMuc> findAllByTenDanhMucContainingAndKichHoat(String keyword, boolean status, Pageable pageable);
}
