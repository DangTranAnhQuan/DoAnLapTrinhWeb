package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "LichSuTrangThaiDon")
public class LichSuTrangThaiDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maLichSu;
    private String tuTrangThai;
    private String denTrangThai;
    @Lob
    private String ghiChu;
    private LocalDateTime thoiDiemThayDoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang")
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaQuanTriVien")
    private User nguoiThucHien;
}
