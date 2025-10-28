package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Order;
import nhom17.OneShop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findByNguoiDung(User nguoiDung, Pageable pageable);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.maDonHang = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    boolean existsByNguoiDung_MaNguoiDung(Integer userId);
    long countByKhuyenMai_MaKhuyenMaiAndTrangThaiNotIn(String maKhuyenMai, List<String> invalidStates);
    long countByNguoiDungAndKhuyenMai_MaKhuyenMaiAndTrangThaiNotIn(User nguoiDung, String maKhuyenMai, List<String> invalidStates);

    @Query(value = "SELECT SUM(d.TongTien) AS TotalRevenue, COUNT(d.MaDonHang) AS TotalOrders " +
            "FROM DonHang d " +
            "JOIN VanChuyen v ON d.MaDonHang = v.MaDonHang " +
            "WHERE d.TrangThai = N'Đã giao' " +
            "AND v.GiaoLuc BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    List<Object[]> findKpiDataBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    @Query(value = "SELECT COALESCE(SUM(dt.SoLuong), 0) AS TotalProductsSold, COALESCE(SUM(dt.SoLuong * ctn.GiaNhapTrungBinh), 0) AS TotalCOGS " +
            "FROM DonHang d " +
            "JOIN VanChuyen v ON d.MaDonHang = v.MaDonHang " +
            "JOIN DonHang_ChiTiet dt ON d.MaDonHang = dt.MaDonHang " +
            "LEFT JOIN ( " +
            "    SELECT MaSanPham, AVG(GiaNhap) AS GiaNhapTrungBinh " +
            "    FROM ChiTietPhieuNhap " +
            "    GROUP BY MaSanPham " +
            ") ctn ON dt.MaSanPham = ctn.MaSanPham " +
            "WHERE d.TrangThai = N'Đã giao' " +
            "AND v.GiaoLuc BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    List<Object[]> findProductsAndCogsDataBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);


    @Query(value = "SELECT CAST(v.GiaoLuc AS DATE) AS Ngay, SUM(d.TongTien) AS DoanhThu " +
            "FROM DonHang d " +
            "JOIN VanChuyen v ON d.MaDonHang = v.MaDonHang " +
            "WHERE d.TrangThai = N'Đã giao' " +
            "AND v.GiaoLuc BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(v.GiaoLuc AS DATE) " +
            "ORDER BY Ngay",
            nativeQuery = true)
    List<Object[]> findRevenueByDayBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    @Query(value = "SELECT sp.TenSanPham, sp.HinhAnh, SUM(dt.SoLuong) AS TongSoLuong, SUM(dt.ThanhTien) AS TongDoanhThu " +
            "FROM DonHang d " +
            "JOIN VanChuyen v ON d.MaDonHang = v.MaDonHang " +
            "JOIN DonHang_ChiTiet dt ON d.MaDonHang = dt.MaDonHang " +
            "JOIN SanPham sp ON dt.MaSanPham = sp.MaSanPham " +
            "WHERE d.TrangThai = N'Đã giao' " +
            "AND v.GiaoLuc BETWEEN :startDate AND :endDate " +
            "GROUP BY sp.MaSanPham, sp.TenSanPham, sp.HinhAnh " +
            "ORDER BY TongSoLuong DESC " +
            "OFFSET 0 ROWS FETCH NEXT :limit ROWS ONLY",
            nativeQuery = true)
    List<Object[]> findTopSellingProductsBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate, @Param("limit") int limit);

    @Query(value = "SELECT dm.TenDanhMuc, SUM(dt.ThanhTien) AS TongDoanhThu " +
            "FROM DonHang d " +
            "JOIN VanChuyen v ON d.MaDonHang = v.MaDonHang " +
            "JOIN DonHang_ChiTiet dt ON d.MaDonHang = dt.MaDonHang " +
            "JOIN SanPham sp ON dt.MaSanPham = sp.MaSanPham " +
            "JOIN DanhMuc dm ON sp.MaDanhMuc = dm.MaDanhMuc " +
            "WHERE d.TrangThai = N'Đã giao' " +
            "AND v.GiaoLuc BETWEEN :startDate AND :endDate " +
            "GROUP BY dm.MaDanhMuc, dm.TenDanhMuc " +
            "ORDER BY TongDoanhThu DESC",
            nativeQuery = true)
    List<Object[]> findRevenueByCategoryBetween(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

    @Query(value = "SELECT CAST(CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END AS BIT) " +
            "FROM DonHang o " +
            "JOIN DonHang_ChiTiet od ON o.MaDonHang = od.MaDonHang " +
            "WHERE o.MaNguoiDung = :userId AND od.MaSanPham = :productId AND o.TrangThai = N'Đã giao'",
            nativeQuery = true)
    boolean hasCompletedPurchase(@Param("userId") Integer userId, @Param("productId") Integer productId);

    @Query(value = "SELECT * FROM DonHang o WHERE o.MaNguoiDung = :userId " +
            "AND NOT (o.PhuongThucThanhToan = N'ONLINE' AND o.TrangThaiThanhToan = N'Chưa thanh toán') /*#pageable*/",
            countQuery = "SELECT count(*) FROM DonHang o WHERE o.MaNguoiDung = :userId " +
                    "AND NOT (o.PhuongThucThanhToan = N'ONLINE' AND o.TrangThaiThanhToan = N'Chưa thanh toán')",
            nativeQuery = true) // Đặt thành true
        // Đổi tham số User thành userId (vì native query dùng ID)
    Page<Order> findOrderHistoryForUserNative(@Param("userId") Integer userId, Pageable pageable);
}