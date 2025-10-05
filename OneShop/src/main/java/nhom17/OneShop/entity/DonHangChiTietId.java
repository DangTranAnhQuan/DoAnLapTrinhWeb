package nhom17.OneShop.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DonHangChiTietId implements Serializable {
    private Long donHang;
    private Integer sanPham;
}
