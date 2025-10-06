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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

import java.util.List;
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

        if (StringUtils.hasText(productRequest.getHinhAnh())) {
            String oldImage = sanPham.getHinhAnh();
            sanPham.setHinhAnh(productRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(sanPham.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        }

        DanhMuc danhMuc = categoryRepository.findById(productRequest.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + productRequest.getMaDanhMuc()));
        ThuongHieu thuongHieu = brandRepository.findById(productRequest.getMaThuongHieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với ID: " + productRequest.getMaThuongHieu()));

        sanPham.setDanhMuc(danhMuc);
        sanPham.setThuongHieu(thuongHieu);

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
    
    @Override
    public Page<SanPham> searchUserProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sortOption, int page, int size) {
        int pageNumber = page > 0 ? page - 1 : 0;

        Sort sort;
        // ... (code switch/case để tạo Sort giữ nguyên như cũ)
        if (sortOption == null || sortOption.isEmpty()) {
            sort = Sort.by("ngayTao").descending(); 
        } else {
             switch (sortOption) {
                case "price_asc": sort = Sort.by("giaBan").ascending(); break;
                case "price_desc": sort = Sort.by("giaBan").descending(); break;
                case "oldest": sort = Sort.by("ngayTao").ascending(); break;
                case "newest": default: sort = Sort.by("ngayTao").descending(); break;
            }
        }
        
        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        // Sử dụng Specification để tạo câu truy vấn động
        Specification<SanPham> spec = Specification.where((root, query, cb) -> cb.isTrue(root.get("kichHoat")));

        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("danhMuc").get("maDanhMuc"), categoryId));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("giaBan"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("giaBan"), maxPrice));
        }

        return productRepository.findAll(spec, pageable);
    }

}