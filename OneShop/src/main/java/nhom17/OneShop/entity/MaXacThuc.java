package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "MaXacThuc")
public class MaXacThuc {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID maOtp;
    private String maSo;
    private String mucDich;
    private LocalDateTime hetHanLuc;
    private boolean daSuDung;
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung", nullable = false)
    private User nguoiDung;
}
