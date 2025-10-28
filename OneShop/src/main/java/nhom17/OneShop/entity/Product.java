package nhom17.OneShop.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.Formula;

@Data
@Entity
@Table(name = "SanPham")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSanPham;
    private String tenSanPham;
    @Lob
    private String moTa;
    private BigDecimal giaBan;
    private BigDecimal giaNiemYet;
    private Integer hanSuDung;
    private String hinhAnh;
    private boolean kichHoat;
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "MaDanhMuc")
    private Category danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaThuongHieu")
    @JsonIgnore
    private Brand thuongHieu;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Rating> danhSachRating;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }

    @OneToOne(mappedBy = "sanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Inventory inventory;
    @Formula("(SELECT SUM(od.SoLuong) " +
            " FROM DonHang_ChiTiet od JOIN DonHang o ON od.MaDonHang = o.MaDonHang " +
            " WHERE od.MaSanPham = MaSanPham AND o.TrangThai = N'Đã giao')")
    private Long totalSold;

    @Formula("(SELECT COUNT(*) FROM DanhGia dg WHERE dg.MaSanPham = MaSanPham)")
    private int reviewCount;

    @Formula("(SELECT AVG(CAST(dg.DiemDanhGia AS DECIMAL(10,2))) " +
            " FROM DanhGia dg WHERE dg.MaSanPham = MaSanPham)")
    private Double averageRating;
}
