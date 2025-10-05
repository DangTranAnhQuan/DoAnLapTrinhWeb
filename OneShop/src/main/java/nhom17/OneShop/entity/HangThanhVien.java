package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "HangThanhVien")
public class HangThanhVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHangThanhVien;
    private String tenHang;
    private Integer diemToiThieu;
    private BigDecimal phanTramGiamGia;
}
