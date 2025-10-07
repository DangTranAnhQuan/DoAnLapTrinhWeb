package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
//    private int hanSuDung;
    private Integer hanSuDung;
    private String hinhAnh;
    private boolean kichHoat;
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDanhMuc")
    private Category danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaThuongHieu")
    private Brand thuongHieu;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> danhSachRating;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }
}
