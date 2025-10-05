package nhom17.OneShop.request;

import lombok.Data;

@Data
public class BrandRequest {
    private Integer maThuongHieu;
    private String tenThuongHieu;
    private String hinhAnh;
    private String moTa;
    private boolean kichHoat;
}
