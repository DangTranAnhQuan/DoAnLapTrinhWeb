package nhom17.OneShop.request;

import lombok.Data;

@Data
public class CategoryRequest {
    private Integer maDanhMuc;
    private String tenDanhMuc;
    private String hinhAnh;
    private boolean kichHoat;
}
