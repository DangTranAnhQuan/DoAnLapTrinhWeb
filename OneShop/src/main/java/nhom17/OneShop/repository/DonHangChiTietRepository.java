package nhom17.OneShop.repository;

import nhom17.OneShop.entity.DonHang_ChiTiet;
import nhom17.OneShop.entity.DonHangChiTietId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonHangChiTietRepository extends JpaRepository<DonHang_ChiTiet, DonHangChiTietId> {
}