package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "DonHang_ChiTiet")
@IdClass(DonHangChiTietId.class)
public class DonHang_ChiTiet {
    private String tenSanPham;
    private BigDecimal donGia;
    private Integer soLuong;

    @Formula("(SoLuong * DonGia)")
    private BigDecimal thanhTien;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDonHang")
    private DonHang donHang;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSanPham")
    private Product sanPham;
}
