package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Brand;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.request.BrandRequest;
import nhom17.OneShop.service.BrandService;
import nhom17.OneShop.service.StorageService;
import nhom17.OneShop.specification.BrandSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Page<Brand> searchAndFilter(String keyword, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maThuongHieu").ascending());

        Specification<Brand> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(keyword)) {
            spec = spec.and(BrandSpecification.hasKeyword(keyword));
        }
        if (status != null) {
            spec = spec.and(BrandSpecification.hasStatus(status));
        }

        return brandRepository.findAll(spec, pageable);
    }

    @Override
    public Brand findById(int id) {
        return brandRepository.findById(id).orElse(null);
    }

    @Override
    public void save(BrandRequest brandRequest) {
        Brand brand;
        if (brandRequest.getMaThuongHieu() != null) {
            brand = brandRepository.findById(brandRequest.getMaThuongHieu()).orElse(new Brand());
        } else {
            brand = new Brand();
        }

        // Xử lý ảnh
        if (StringUtils.hasText(brandRequest.getHinhAnh())) {
            String oldImage = brand.getHinhAnh();
            brand.setHinhAnh(brandRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(brand.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        }

        // Cập nhật các thông tin khác
        brand.setTenThuongHieu(brandRequest.getTenThuongHieu());
        brand.setMoTa(brandRequest.getMoTa());
        brand.setKichHoat(brandRequest.isKichHoat());

        brandRepository.save(brand);
    }

    @Override
    public void delete(int id) {
        Optional<Brand> brandOpt = brandRepository.findById(id);
        if (brandOpt.isPresent()) {
            Brand thuongHieu = brandOpt.get();
            if (StringUtils.hasText(thuongHieu.getHinhAnh())) {
                storageService.deleteFile(thuongHieu.getHinhAnh());
            }
            brandRepository.deleteById(id);
        }
    }
}
