package nhom17.OneShop.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoucherRequest {
    private String maKhuyenMai;
    private String tenChienDich;
    private Integer kieuApDung;
    private BigDecimal giaTri;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime batDauLuc;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ketThucLuc;

    private BigDecimal tongTienToiThieu;
    private BigDecimal giamToiDa;
    private Integer gioiHanTongSoLan;
    private Integer gioiHanMoiNguoi;
    private Integer trangThai;
}
