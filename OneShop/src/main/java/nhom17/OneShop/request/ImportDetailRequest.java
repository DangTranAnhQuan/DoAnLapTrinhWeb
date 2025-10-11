package nhom17.OneShop.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ImportDetailRequest {
    private Integer maSanPham;
    private int soLuong;
    private BigDecimal giaNhap;
}
