package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NhatKyHeThong")
public class NhatKyHeThong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maNhatKy;
    private String hanhDong;
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDungThucHien")
    private User nguoiDungThucHien;
}
