//package nhom17.OneShop.repository;
//
//import nhom17.OneShop.entity.Product;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
//    Optional<Product> findByTenSanPhamIgnoreCase(String tenSanPham);
//    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);
//    boolean existsByTenSanPhamIgnoreCaseAndMaSanPhamNot(String tenSanPham, Integer productId);
//    boolean existsByDanhMuc_MaDanhMuc(Integer categoryId);
//    boolean existsByThuongHieu_MaThuongHieu(Integer brandId);
//    List<Product> findTop8ByKichHoatIsTrueOrderByNgayTaoDesc();
//
//    @Query("SELECT p FROM Product p WHERE p.kichHoat = true AND p.giaNiemYet > p.giaBan ORDER BY ((p.giaNiemYet - p.giaBan) / p.giaNiemYet) DESC")
//    List<Product> findTopDiscountedProducts(Pageable pageable);
//
////    User
//    @Query("SELECT p FROM Product p WHERE p.kichHoat = true AND (" +
//            "LOWER(p.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.danhMuc.tenDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(p.thuongHieu.tenThuongHieu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<Product> searchForUser(@Param("keyword") String keyword, Pageable pageable);
//
//    @Query(value = "SELECT TOP (:limit) p.* " +
//            "FROM SanPham p " +
//            "JOIN DonHang_ChiTiet od ON p.MaSanPham = od.MaSanPham " +
//            "JOIN DonHang o ON od.MaDonHang = o.MaDonHang " +
//            "WHERE o.TrangThai = N'Đã giao' AND p.KichHoat = 1 " +
//            "GROUP BY p.MaSanPham, p.TenSanPham, p.MaDanhMuc, p.MaThuongHieu, p.MoTa, p.GiaBan, p.GiaNiemYet, p.HanSuDung, p.HinhAnh, p.KichHoat, p.NgayTao " +
//            "ORDER BY SUM(od.SoLuong) DESC",
//            nativeQuery = true)
//    List<Product> findTopSellingProducts(@Param("limit") int limit);
//}
package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByTenSanPhamIgnoreCase(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);
    boolean existsByTenSanPhamIgnoreCaseAndMaSanPhamNot(String tenSanPham, Integer productId);
    boolean existsByDanhMuc_MaDanhMuc(Integer categoryId);
    boolean existsByThuongHieu_MaThuongHieu(Integer brandId);

    List<Product> findTop8ByKichHoatIsTrueOrderByNgayTaoDesc();


    @Query(value = "SELECT p.*, " +
            "(SELECT SUM(od.SoLuong) FROM DonHang_ChiTiet od JOIN DonHang o ON od.MaDonHang = o.MaDonHang WHERE od.MaSanPham = p.MaSanPham AND o.TrangThai = N'Đã giao') AS totalSold, " +
            "(SELECT COUNT(*) FROM DanhGia dg WHERE dg.MaSanPham = p.MaSanPham) AS reviewCount, " +
            "(SELECT AVG(CAST(dg.DiemDanhGia AS DECIMAL(10,2))) FROM DanhGia dg WHERE dg.MaSanPham = p.MaSanPham) AS averageRating " +
            "FROM SanPham p " +
            "WHERE p.KichHoat = 1 AND p.GiaNiemYet > p.GiaBan " +
            "ORDER BY ((p.GiaNiemYet - p.GiaBan) / p.GiaNiemYet) DESC",
            nativeQuery = true)
    List<Product> findTopDiscountedProducts(Pageable pageable);

    @Query(value = "SELECT p.*, " +

            "(SELECT SUM(od.SoLuong) FROM DonHang_ChiTiet od JOIN DonHang o ON od.MaDonHang = o.MaDonHang WHERE od.MaSanPham = p.MaSanPham AND o.TrangThai = N'Đã giao') AS totalSold, " +
            "(SELECT COUNT(*) FROM DanhGia dg WHERE dg.MaSanPham = p.MaSanPham) AS reviewCount, " +
            "(SELECT AVG(CAST(dg.DiemDanhGia AS DECIMAL(10,2))) FROM DanhGia dg WHERE dg.MaSanPham = p.MaSanPham) AS averageRating " +
            "FROM SanPham p " +
            "LEFT JOIN DanhMuc d ON p.MaDanhMuc = d.MaDanhMuc " +
            "LEFT JOIN ThuongHieu th ON p.MaThuongHieu = th.MaThuongHieu " +
            "WHERE p.KichHoat = 1 AND (" +
            "LOWER(p.TenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.TenDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(th.TenThuongHieu) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(p.MaSanPham) " +
                    "FROM SanPham p " +
                    "LEFT JOIN DanhMuc d ON p.MaDanhMuc = d.MaDanhMuc " +
                    "LEFT JOIN ThuongHieu th ON p.MaThuongHieu = th.MaThuongHieu " +
                    "WHERE p.KichHoat = 1 AND (" +
                    "LOWER(p.TenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(d.TenDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(th.TenThuongHieu) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            nativeQuery = true)
    Page<Product> searchForUser(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT TOP (:limit) p.*, " +
            "(SELECT SUM(od.SoLuong) FROM DonHang_ChiTiet od JOIN DonHang o ON od.MaDonHang = o.MaDonHang WHERE od.MaSanPham = p.MaSanPham AND o.TrangThai = N'Đã giao') AS totalSold, " +
            "(SELECT COUNT(*) FROM DanhGia dg WHERE dg.MaSanPham = p.MaSanPham) AS reviewCount, " +
            "(SELECT AVG(CAST(dg.DiemDanhGia AS DECIMAL(10,2))) FROM DanhGia dg WHERE dg.MaSanPham = p.MaSanPham) AS averageRating " +
            "FROM SanPham p " +
            "JOIN DonHang_ChiTiet od ON p.MaSanPham = od.MaSanPham " +
            "JOIN DonHang o ON od.MaDonHang = o.MaDonHang " +
            "WHERE o.TrangThai = N'Đã giao' AND p.KichHoat = 1 " +
            "GROUP BY p.MaSanPham, p.TenSanPham, p.MaDanhMuc, p.MaThuongHieu, p.MoTa, p.GiaBan, p.GiaNiemYet, p.HanSuDung, p.HinhAnh, p.KichHoat, p.NgayTao " +
            "ORDER BY SUM(od.SoLuong) DESC",
            nativeQuery = true)
    List<Product> findTopSellingProducts(@Param("limit") int limit);
}