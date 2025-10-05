package nhom17.OneShop.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SanPhamYeuThich")
@IdClass(SanPhamYeuThichId.class)
public class SanPhamYeuThich {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung")
    private NguoiDung nguoiDung;
}
