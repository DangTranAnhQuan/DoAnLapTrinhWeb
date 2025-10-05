package nhom17.OneShop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "VaiTro")
public class VaiTro {
    @Id
    private Integer maVaiTro;
    private String tenVaiTro;
}
