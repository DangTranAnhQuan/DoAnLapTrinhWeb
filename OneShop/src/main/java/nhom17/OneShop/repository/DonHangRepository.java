package nhom17.OneShop.repository;

import nhom17.OneShop.entity.DonHang;
import nhom17.OneShop.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    List<DonHang> findByNguoiDungOrderByNgayDatDesc(NguoiDung nguoiDung);
    @Query("SELECT d FROM DonHang d LEFT JOIN FETCH d.donHangChiTiets WHERE d.maDonHang = :orderId")
    Optional<DonHang> findByIdWithDetails(@Param("orderId") Long orderId);
}