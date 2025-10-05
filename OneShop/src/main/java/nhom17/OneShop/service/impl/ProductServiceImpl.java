package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.DanhMuc;
import nhom17.OneShop.entity.SanPham;
import nhom17.OneShop.entity.ThuongHieu;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.repository.CategoryRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.request.ProductRequest;
import nhom17.OneShop.service.ProductService;
import nhom17.OneShop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private StorageService storageService;

    @Override
    public Page<SanPham> searchProducts(String keyword, Boolean status, String sort, int page, int size) {
        // 1. Xử lý logic sắp xếp
        Sort sortable = Sort.by("maSanPham").ascending();
        if (sort != null && !sort.isEmpty()) {
            switch (sort) {
                case "price_asc":
                    sortable = Sort.by("giaBan").ascending();
                    break;
                case "price_desc":
                    sortable = Sort.by("giaBan").descending();
                    break;
            }
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortable);

        boolean hasKeyword = StringUtils.hasText(keyword);
        if (hasKeyword && status != null) {
            return productRepository.findByTenSanPhamContainingAndKichHoat(keyword, status, pageable);
        } else if (hasKeyword) {
            return productRepository.findByTenSanPhamContaining(keyword, pageable);
        } else if (status != null) {
            return productRepository.findByKichHoat(status, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    @Override
    public SanPham findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void save(ProductRequest productRequest) {
        SanPham sanPham;
        if (productRequest.getMaSanPham() != null) {
            sanPham = productRepository.findById(productRequest.getMaSanPham()).orElse(new SanPham());
        } else {
            sanPham = new SanPham();
        }

        // Xử lý ảnh
        if (StringUtils.hasText(productRequest.getHinhAnh())) {
            String oldImage = sanPham.getHinhAnh();
            sanPham.setHinhAnh(productRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(sanPham.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        }

        // Lấy đối tượng DanhMuc và ThuongHieu từ ID
        DanhMuc danhMuc = categoryRepository.findById(productRequest.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + productRequest.getMaDanhMuc()));
        ThuongHieu thuongHieu = brandRepository.findById(productRequest.getMaThuongHieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với ID: " + productRequest.getMaThuongHieu()));

        sanPham.setDanhMuc(danhMuc);
        sanPham.setThuongHieu(thuongHieu);

        // Cập nhật các thông tin khác
        sanPham.setTenSanPham(productRequest.getTenSanPham());
        sanPham.setMoTa(productRequest.getMoTa());
        sanPham.setGiaBan(productRequest.getGiaBan());
        sanPham.setGiaNiemYet(productRequest.getGiaNiemYet());
        sanPham.setHanSuDung(productRequest.getHanSuDung());
        sanPham.setKichHoat(productRequest.isKichHoat());

        productRepository.save(sanPham);
    }

    @Override
    public void delete(int id) {
        Optional<SanPham> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            SanPham sanPham = productOpt.get();
            if (StringUtils.hasText(sanPham.getHinhAnh())) {
                storageService.deleteFile(sanPham.getHinhAnh());
            }
            productRepository.deleteById(id);
        }
    }
}