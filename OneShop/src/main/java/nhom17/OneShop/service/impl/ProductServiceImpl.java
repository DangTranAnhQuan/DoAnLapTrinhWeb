package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Brand;
import nhom17.OneShop.entity.Category;
import nhom17.OneShop.entity.Product;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.repository.CategoryRepository;
import nhom17.OneShop.repository.ProductRepository;
import nhom17.OneShop.request.ProductRequest;
import nhom17.OneShop.service.ProductService;
import nhom17.OneShop.service.StorageService;
import nhom17.OneShop.specification.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
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

    // Phương thức này dành cho trang admin, giữ nguyên
    @Override
    public Page<Product> searchProducts(String keyword, Boolean status, Integer categoryId, Integer brandId, String sort, int page, int size) {
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
        Specification<Product> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (StringUtils.hasText(keyword)) {
            spec = spec.and(ProductSpecification.hasKeyword(keyword));
        }
        if (status != null) {
            spec = spec.and(ProductSpecification.hasStatus(status));
        }
        if (categoryId != null) {
            spec = spec.and(ProductSpecification.inCategory(categoryId));
        }
        if (brandId != null) {
            spec = spec.and(ProductSpecification.inBrand(brandId));
        }
        return productRepository.findAll(spec, pageable);
    }

    @Override
    public Product findById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void save(ProductRequest productRequest) {
        Product product;
        if (productRequest.getMaSanPham() != null) {
            // Đây là trường hợp CẬP NHẬT sản phẩm đã có
            product = productRepository.findById(productRequest.getMaSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productRequest.getMaSanPham()));
        } else {
            // Đây là trường hợp THÊM MỚI sản phẩm
            product = new Product();
            // Gán ngày tạo là thời điểm hiện tại
            product.setNgayTao(LocalDateTime.now());
        }

        if (StringUtils.hasText(productRequest.getHinhAnh())) {
            String oldImage = product.getHinhAnh();
            product.setHinhAnh(productRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(product.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        }

        Category danhMuc = categoryRepository.findById(productRequest.getMaDanhMuc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + productRequest.getMaDanhMuc()));
        Brand thuongHieu = brandRepository.findById(productRequest.getMaThuongHieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với ID: " + productRequest.getMaThuongHieu()));
        product.setDanhMuc(danhMuc);
        product.setThuongHieu(thuongHieu);
        product.setTenSanPham(productRequest.getTenSanPham());
        product.setMoTa(productRequest.getMoTa());
        product.setGiaBan(productRequest.getGiaBan());
        product.setGiaNiemYet(productRequest.getGiaNiemYet());
        product.setHanSuDung(productRequest.getHanSuDung());
        product.setKichHoat(productRequest.isKichHoat());
        productRepository.save(product);
    }

    @Override
    public void delete(int id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product sanPham = productOpt.get();
            if (StringUtils.hasText(sanPham.getHinhAnh())) {
                storageService.deleteFile(sanPham.getHinhAnh());
            }
            productRepository.deleteById(id);
        }
    }

    @Override
    public Page<Product> searchUserProducts(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String sortOption, List<Integer> brandIds, int page, int size) {
        int pageNumber = page > 0 ? page - 1 : 0;

        Sort sort;
        if (sortOption == null || sortOption.isEmpty() || sortOption.equals("newest")) {
            sort = Sort.by("ngayTao").descending();
        } else {
            switch (sortOption) {
                case "price_asc":
                    sort = Sort.by("giaBan").ascending();
                    break;
                case "price_desc":
                    sort = Sort.by("giaBan").descending();
                    break;
                case "oldest":
                    sort = Sort.by("ngayTao").ascending();
                    break;
                default:
                    sort = Sort.by("ngayTao").descending();
                    break;
            }
        }

        Pageable pageable = PageRequest.of(pageNumber, size, sort);

        // Bắt đầu câu truy vấn với điều kiện cơ bản là sản phẩm phải được kích hoạt
        Specification<Product> spec = (root, query, cb) -> cb.isTrue(root.get("kichHoat"));

        // Thêm các điều kiện lọc nếu chúng tồn tại
        if (categoryId != null) {
            spec = spec.and(ProductSpecification.inCategory(categoryId));
        }
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("giaBan"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("giaBan"), maxPrice));
        }
        if (brandIds != null && !brandIds.isEmpty()) {
            spec = spec.and(ProductSpecification.inBrands(brandIds));
        }

        return productRepository.findAll(spec, pageable);
    }
    @Override
    public Page<Product> searchProductsForUser(String keyword, int page, int size) {
        // Sắp xếp theo ngày tạo mới nhất làm mặc định
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayTao").descending());
        return productRepository.searchForUser(keyword, pageable);
    }
}