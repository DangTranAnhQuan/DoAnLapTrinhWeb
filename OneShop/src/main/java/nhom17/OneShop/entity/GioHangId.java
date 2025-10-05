package nhom17.OneShop.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GioHangId implements Serializable {
    private Integer nguoiDung;
    private Integer sanPham;
}
