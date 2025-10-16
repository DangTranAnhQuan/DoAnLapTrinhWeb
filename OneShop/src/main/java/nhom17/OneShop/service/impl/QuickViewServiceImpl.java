package nhom17.OneShop.service.impl;

import jakarta.transaction.Transactional;
import nhom17.OneShop.dto.QuickViewDTO;
import nhom17.OneShop.entity.Inventory;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.repository.InventoryRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.service.QuickViewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class QuickViewServiceImpl implements QuickViewService {

    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;

    public QuickViewServiceImpl(ProductRepository productRepo,
                                InventoryRepository inventoryRepo) {
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public QuickViewDTO build(Integer productId) {
        Product p = productRepo.findById(productId).orElse(null);
        if (p == null) return null;

        // ====== TỒN KHO (Inventory OneToOne @MapsId) ======
        // Repository của bạn có method: findBySanPham(Product sanPham)
        boolean inStock = inventoryRepo.findBySanPham(p)
                .map(i -> i.getSoLuongTon() != null && i.getSoLuongTon() > 0)
                .orElse(false);

        // ====== ẢNH CHÍNH ======
        String imageUrl = (p.getHinhAnh() == null || p.getHinhAnh().isBlank())
                ? ""
                : (p.getHinhAnh().startsWith("/uploads/")
                ? p.getHinhAnh()
                : "/uploads/" + p.getHinhAnh());

        // ====== GIÁ ======
        long price = p.getGiaBan() != null ? p.getGiaBan().longValue() : 0L;
        long oldPrice = p.getGiaNiemYet() != null ? p.getGiaNiemYet().longValue() : 0L;

        // ====== TÊN / MÔ TẢ NGẮN ======
        String name = p.getTenSanPham();
        String shortDesc = p.getMoTa();

        // ====== THƯƠNG HIỆU / DANH MỤC ======
        String brandName = (p.getThuongHieu() != null) ? p.getThuongHieu().getTenThuongHieu() : null;
        String categoryName = (p.getDanhMuc() != null) ? p.getDanhMuc().getTenDanhMuc() : null;

        // ====== RATING ======
        Integer reviewCount = (p.getDanhSachRating() != null) ? p.getDanhSachRating().size() : 0;
        Double avgRating = null; // có thể bổ sung khi cần

        return new QuickViewDTO(
                p.getMaSanPham(),           // id
                name,                       // name
                brandName,                  // brandName
                categoryName,               // categoryName
                shortDesc,                  // shortDesc
                price,                      // price (VND)
                oldPrice,                   // oldPrice (VND)
                inStock,                    // inStock
                avgRating,                  // rating
                reviewCount,                // reviewCount
                List.of(imageUrl)           // images
        );
    }
}
