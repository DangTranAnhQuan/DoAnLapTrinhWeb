package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NguoiDung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNguoiDung;
    private String email;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String soDienThoai;
    private Integer trangThai;
    private String anhDaiDien;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaVaiTro")
    private VaiTro vaiTro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHangThanhVien")
    private HangThanhVien hangThanhVien;
}
