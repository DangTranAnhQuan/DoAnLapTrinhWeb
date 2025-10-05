package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.DanhMuc;
import nhom17.OneShop.repository.CategoryRepository;
import nhom17.OneShop.request.CategoryRequest;
import nhom17.OneShop.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Page<DanhMuc> searchAndFilter(String keyword, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maDanhMuc").ascending());

        boolean hasKeyword = StringUtils.hasText(keyword);

        if (hasKeyword && status != null) {
            // Trường hợp 1: Có cả keyword và status
            return categoryRepository.findAllByTenDanhMucContainingAndKichHoat(keyword, status, pageable);
        } else if (hasKeyword) {
            // Trường hợp 2: Chỉ có keyword
            return categoryRepository.findAllByTenDanhMucContaining(keyword, pageable);
        } else if (status != null) {
            // Trường hợp 3: Chỉ có status
            return categoryRepository.findAllByKichHoat(status, pageable);
        } else {
            // Trường hợp 4: Không có cả hai, lấy tất cả
            return categoryRepository.findAll(pageable);
        }
    }

    @Override
    public DanhMuc findById(int id) {
        return categoryRepository.findById(id).orElse(null);
    }
    @Override
    public void save(CategoryRequest categoryRequest) {
        DanhMuc danhMuc;

        if (categoryRequest.getMaDanhMuc() != null) {
            danhMuc = categoryRepository.findById(categoryRequest.getMaDanhMuc())
                    .orElse(new DanhMuc());
        } else {
            danhMuc = new DanhMuc();
        }

        // Xử lý ảnh
        if (StringUtils.hasText(categoryRequest.getHinhAnh())) {
            // Có ảnh mới upload
            String oldImage = danhMuc.getHinhAnh();
            danhMuc.setHinhAnh(categoryRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(danhMuc.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        } else if (danhMuc.getMaDanhMuc() != null) {
            // Sửa mà không upload ảnh -> giữ nguyên ảnh cũ
            DanhMuc existing = categoryRepository.findById(danhMuc.getMaDanhMuc()).orElse(null);
            if (existing != null) {
                danhMuc.setHinhAnh(existing.getHinhAnh());
            }
        }

        // Cập nhật các thông tin khác
        danhMuc.setTenDanhMuc(categoryRequest.getTenDanhMuc());
        danhMuc.setKichHoat(categoryRequest.isKichHoat());

        categoryRepository.save(danhMuc);
    }

    @Override
    public void delete(int id) {
        Optional<DanhMuc> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            DanhMuc category = categoryOpt.get();
            // Nếu danh mục có ảnh, thì xóa file ảnh trước
            if (StringUtils.hasText(category.getHinhAnh())) {
                storageService.deleteFile(category.getHinhAnh());
            }
            // Sau đó xóa bản ghi trong database
            categoryRepository.deleteById(id);
        }
    }
}
