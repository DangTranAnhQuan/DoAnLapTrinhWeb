package nhom17.OneShop.repository;

import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.entity.SanPhamYeuThich;
import nhom17.OneShop.entity.SanPhamYeuThichId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<SanPhamYeuThich, SanPhamYeuThichId> {
    List<SanPhamYeuThich> findByNguoiDung(NguoiDung nguoiDung);
    long countByNguoiDung(NguoiDung nguoiDung);
    Optional<SanPhamYeuThich> findByNguoiDungAndSanPham_MaSanPham(NguoiDung nguoiDung, Integer productId);
}