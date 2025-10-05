package nhom17.OneShop.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class KhoHangId implements Serializable {
    private Integer sanPham;
    private Integer phieuNhap;
}
