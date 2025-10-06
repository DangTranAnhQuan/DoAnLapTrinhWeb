package nhom17.OneShop.request;

import lombok.Data;

@Data
public class ShippingCarrierRequest {
    private Integer maNVC;
    private String tenNVC;
    private String soDienThoai;
    private String website;
}
