package nhom17.OneShop.repository;

import nhom17.OneShop.entity.GioHang;
import nhom17.OneShop.entity.GioHangId;
import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, GioHangId> {

    Optional<GioHang> findByNguoiDungAndSanPham(NguoiDung nguoiDung, Product sanPham);
    @Query("SELECT gh FROM GioHang gh JOIN FETCH gh.sanPham WHERE gh.nguoiDung = :nguoiDung")
    List<GioHang> findByNguoiDungWithProduct(@Param("nguoiDung") NguoiDung nguoiDung);

    @Transactional
    void deleteByNguoiDungAndSanPham(NguoiDung nguoiDung, Product sanPham);
}