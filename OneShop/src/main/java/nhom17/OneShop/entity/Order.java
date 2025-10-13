package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DonHang")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDonHang;
    private LocalDateTime ngayDat;
    private String trangThai;
    private String phuongThucThanhToan;
    private String trangThaiThanhToan;
    private BigDecimal tienHang;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private String tenNguoiNhan;
    private String soDienThoaiNhan;
    private String diaChiNhan;
    @Lob
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung")
    private User nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaKhuyenMai")
    private Voucher khuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDiaChiNhan")
    private Address diaChi;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> donHangChiTiets;
}
