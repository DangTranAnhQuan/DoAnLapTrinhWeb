package nhom17.OneShop.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DanhGiaId implements Serializable {
    private Integer sanPham;
    private Integer nguoiDung;
}
