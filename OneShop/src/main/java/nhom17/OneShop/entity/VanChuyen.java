package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "VanChuyen")
public class VanChuyen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maVanChuyen;
    private String maVanDon;
    private LocalDateTime guiLuc;
    private LocalDateTime giaoLuc;
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang")
    private DonHang donHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNVC")
    private NhaVanChuyen nhaVanChuyen;
}
