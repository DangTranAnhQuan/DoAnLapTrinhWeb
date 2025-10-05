package nhom17.OneShop.request;

import lombok.Data;

import java.util.List;

@Data
public class ImportRequest {
    private Integer maPhieuNhap;
    private Integer maNCC;
    private List<ImportDetailRequest> chiTietPhieuNhapList;
}
