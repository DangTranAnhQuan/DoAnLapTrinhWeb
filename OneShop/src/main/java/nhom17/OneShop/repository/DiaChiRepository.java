package nhom17.OneShop.repository;

import nhom17.OneShop.entity.DiaChi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiaChiRepository extends JpaRepository<DiaChi, Integer> {
    List<DiaChi> findByNguoiDung_MaNguoiDung(Integer userId);
}