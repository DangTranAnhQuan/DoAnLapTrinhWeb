package nhom17.OneShop.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductRequest {
    private Integer maSanPham;
    private String tenSanPham;
    private String moTa;
    private BigDecimal giaBan;
    private BigDecimal giaNiemYet;
    private int hanSuDung;

    private String hinhAnh;
    private boolean kichHoat = true; 
    private Integer maDanhMuc;
    private Integer maThuongHieu;
}
