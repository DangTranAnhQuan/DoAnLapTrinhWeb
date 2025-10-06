//package nhom17.OneShop.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@Table(name = "NguoiDung")
//public class NguoiDung {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer maNguoiDung;
//    private String email;
//    private String tenDangNhap;
//    private String matKhau;
//    private String hoTen;
//    private String soDienThoai;
//    private Integer trangThai;
//    private String anhDaiDien;
//    private LocalDateTime ngayTao;
//    private LocalDateTime ngayCapNhat;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "MaVaiTro")
//    private VaiTro vaiTro;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "MaHangThanhVien")
//    private HangThanhVien hangThanhVien;
//}

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
    @Column(name = "MaNguoiDung")
    private Integer maNguoiDung;

    @Column(name = "Email")
    private String email;

    @Column(name = "TenDangNhap")
    private String tenDangNhap;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "TrangThai")
    private Integer trangThai;

    @Column(name = "AnhDaiDien")
    private String anhDaiDien;

    @Column(name = "NgayTao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NgayCapNhat")
    private LocalDateTime ngayCapNhat;

    // SỬA LẠI FetchType.LAZY thành FetchType.EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaVaiTro")
    private VaiTro vaiTro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHangThanhVien")
    private HangThanhVien hangThanhVien;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngayCapNhat = LocalDateTime.now();
    }
}
