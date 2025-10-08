package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Category;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.repository.CategoryRepository;
import nhom17.OneShop.request.CategoryRequest;
import nhom17.OneShop.service.CategoryService;
import nhom17.OneShop.service.StorageService;
import nhom17.OneShop.specification.CategorySpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Page<Category> searchAndFilter(String keyword, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maDanhMuc").ascending());

        Specification<Category> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(keyword)) {
            spec = spec.and(CategorySpecification.hasKeyword(keyword));
        }
        if (status != null) {
            spec = spec.and(CategorySpecification.hasStatus(status));
        }

        return categoryRepository.findAll(spec, pageable);
    }

    @Override
    public Category findById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }
    @Override
    public void save(CategoryRequest categoryRequest) {
        Optional<Category> existingCategory = categoryRepository.findByTenDanhMucIgnoreCase(categoryRequest.getTenDanhMuc());
        if (existingCategory.isPresent()) {
            // Nếu tìm thấy, kiểm tra xem có phải là chính nó không (trường hợp sửa)
            if (categoryRequest.getMaDanhMuc() == null || !existingCategory.get().getMaDanhMuc().equals(categoryRequest.getMaDanhMuc())) {
                throw new DuplicateRecordException("Tên danh mục '" + categoryRequest.getTenDanhMuc() + "' đã tồn tại.");
            }
        }

        Category category;

        if (categoryRequest.getMaDanhMuc() != null) {
            category = categoryRepository.findById(categoryRequest.getMaDanhMuc())
                    .orElse(new Category());
        } else {
            category = new Category();
        }

        // Xử lý ảnh
        if (StringUtils.hasText(categoryRequest.getHinhAnh())) {
            // Có ảnh mới upload
            String oldImage = category.getHinhAnh();
            category.setHinhAnh(categoryRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(category.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        } else if (category.getMaDanhMuc() != null) {
            // Sửa mà không upload ảnh -> giữ nguyên ảnh cũ
            Category existing = categoryRepository.findById(category.getMaDanhMuc()).orElse(null);
            if (existing != null) {
                category.setHinhAnh(existing.getHinhAnh());
            }
        }

        // Cập nhật các thông tin khác
        category.setTenDanhMuc(categoryRequest.getTenDanhMuc());
        category.setKichHoat(categoryRequest.isKichHoat());

        categoryRepository.save(category);
    }

    @Override
    public void delete(int id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            // Nếu danh mục có ảnh, thì xóa file ảnh trước
            if (StringUtils.hasText(category.getHinhAnh())) {
                storageService.deleteFile(category.getHinhAnh());
            }
            // Sau đó xóa bản ghi trong database
            categoryRepository.deleteById(id);
        }
    }
    
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
