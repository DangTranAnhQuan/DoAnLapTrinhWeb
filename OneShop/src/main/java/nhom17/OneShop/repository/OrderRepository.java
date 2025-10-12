package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.request.TopSellingProductDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    boolean existsByNguoiDung_MaNguoiDung(Integer userId);

    @Query(value = "SELECT COALESCE(SUM(o.TongTien), 0), COUNT(o.MaDonHang) " +
            "FROM DonHang o " +
            "WHERE o.TrangThai = N'Đã giao' AND o.NgayDat >= :startDate AND o.NgayDat < :endDate",
            nativeQuery = true)
    List<Object[]> findKpiDataBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    // ✅ SỬA LẠI HOÀN TOÀN LOGIC TÍNH GIÁ VỐN (COGS)
    @Query(value = "WITH LatestImportPrice AS ( " +
            "    SELECT MaSanPham, GiaNhap, ROW_NUMBER() OVER(PARTITION BY MaSanPham ORDER BY MaPhieuNhap DESC) as rn " +
            "    FROM ChiTietPhieuNhap " +
            ") " +
            "SELECT COALESCE(SUM(od.SoLuong), 0), COALESCE(SUM(od.SoLuong * lip.GiaNhap), 0) " +
            "FROM DonHang o " +
            "JOIN DonHang_ChiTiet od ON o.MaDonHang = od.MaDonHang " +
            "LEFT JOIN LatestImportPrice lip ON od.MaSanPham = lip.MaSanPham AND lip.rn = 1 " +
            "WHERE o.TrangThai = N'Đã giao' AND o.NgayDat >= :startDate AND o.NgayDat < :endDate",
            nativeQuery = true)
    List<Object[]> findProductsAndCogsDataBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);


    @Query(value = "SELECT CAST(o.NgayDat AS DATE) as OrderDate, SUM(o.TongTien) as DailyRevenue " +
            "FROM DonHang o " +
            "WHERE o.TrangThai = N'Đã giao' AND o.NgayDat >= :startDate AND o.NgayDat < :endDate " +
            "GROUP BY CAST(o.NgayDat AS DATE) ORDER BY OrderDate ASC",
            nativeQuery = true)
    List<Object[]> findRevenueByDayBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    @Query(value = "WITH RankedProducts AS ( " +
            "    SELECT p.TenSanPham, p.HinhAnh, SUM(od.SoLuong) as TotalQuantity, SUM(od.ThanhTien) as TotalRevenue, " +
            "           ROW_NUMBER() OVER (ORDER BY SUM(od.SoLuong) DESC) as rn " +
            "    FROM DonHang o JOIN DonHang_ChiTiet od ON o.MaDonHang = od.MaDonHang JOIN SanPham p ON od.MaSanPham = p.MaSanPham " +
            "    WHERE o.TrangThai = N'Đã giao' AND o.NgayDat >= :startDate AND o.NgayDat < :endDate " +
            "    GROUP BY p.TenSanPham, p.HinhAnh " +
            ") " +
            "SELECT rp.TenSanPham, rp.HinhAnh, rp.TotalQuantity, rp.TotalRevenue FROM RankedProducts rp WHERE rp.rn <= :limit",
            nativeQuery = true)
    List<Object[]> findTopSellingProductsBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, @Param("limit") int limit);

    @Query(value = "SELECT c.TenDanhMuc, SUM(od.ThanhTien) as CategoryRevenue " +
            "FROM DonHang o " +
            "JOIN DonHang_ChiTiet od ON o.MaDonHang = od.MaDonHang " +
            "JOIN SanPham p ON od.MaSanPham = p.MaSanPham " +
            "JOIN DanhMuc c ON p.MaDanhMuc = c.MaDanhMuc " +
            "WHERE o.TrangThai = N'Đã giao' AND o.NgayDat >= :startDate AND o.NgayDat < :endDate " +
            "GROUP BY c.TenDanhMuc ORDER BY CategoryRevenue DESC",
            nativeQuery = true)
    List<Object[]> findRevenueByCategoryBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);
}