package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "DanhMuc")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private String hinhAnh;
    private boolean kichHoat;
}
