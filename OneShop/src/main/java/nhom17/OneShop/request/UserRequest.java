package nhom17.OneShop.request;

import lombok.Data;

@Data
public class UserRequest {
    private Integer maNguoiDung;
    private String email;
    private String tenDangNhap;
    private String matKhau;
    private String hoTen;
    private String soDienThoai;
    private Integer trangThai;
    private String anhDaiDien;
    private Integer maVaiTro;
    private Integer maHangThanhVien;
}
