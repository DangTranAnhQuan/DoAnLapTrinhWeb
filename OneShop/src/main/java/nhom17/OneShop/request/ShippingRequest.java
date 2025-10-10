package nhom17.OneShop.request;

import lombok.Data;

@Data
public class ShippingRequest {
    private Long maVanChuyen;
    private Long maDonHang;
    private Integer maNVC;
    private String trangThai;
}
