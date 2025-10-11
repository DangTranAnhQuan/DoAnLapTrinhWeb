package nhom17.OneShop.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MembershipTierRequest {
    private Integer maHangThanhVien;
    private String tenHang;
    private Integer diemToiThieu;
    private BigDecimal phanTramGiamGia;
}
