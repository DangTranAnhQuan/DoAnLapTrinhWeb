package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "NhaCungCap")
public class NhaCungCap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;
}
