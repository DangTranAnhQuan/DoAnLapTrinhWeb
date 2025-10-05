package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.ThuongHieu;
import nhom17.OneShop.repository.BrandRepository;
import nhom17.OneShop.request.BrandRequest;
import nhom17.OneShop.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public Page<ThuongHieu> searchAndFilter(String keyword, Boolean status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maThuongHieu").ascending());
        boolean hasKeyword = StringUtils.hasText(keyword);

        if (hasKeyword && status != null) {
            return brandRepository.findAllByTenThuongHieuContainingAndKichHoat(keyword, status, pageable);
        } else if (hasKeyword) {
            return brandRepository.findAllByTenThuongHieuContaining(keyword, pageable);
        } else if (status != null) {
            return brandRepository.findAllByKichHoat(status, pageable);
        } else {
            return brandRepository.findAll(pageable);
        }
    }

    @Override
    public ThuongHieu findById(int id) {
        return brandRepository.findById(id).orElse(null);
    }

    @Override
    public void save(BrandRequest brandRequest) {
        ThuongHieu thuongHieu;
        if (brandRequest.getMaThuongHieu() != null) {
            thuongHieu = brandRepository.findById(brandRequest.getMaThuongHieu()).orElse(new ThuongHieu());
        } else {
            thuongHieu = new ThuongHieu();
        }

        // Xử lý ảnh
        if (StringUtils.hasText(brandRequest.getHinhAnh())) {
            String oldImage = thuongHieu.getHinhAnh();
            thuongHieu.setHinhAnh(brandRequest.getHinhAnh());
            if (StringUtils.hasText(oldImage) && !oldImage.equals(thuongHieu.getHinhAnh())) {
                storageService.deleteFile(oldImage);
            }
        }

        // Cập nhật các thông tin khác
        thuongHieu.setTenThuongHieu(brandRequest.getTenThuongHieu());
        thuongHieu.setMoTa(brandRequest.getMoTa());
        thuongHieu.setKichHoat(brandRequest.isKichHoat());

        brandRepository.save(thuongHieu);
    }

    @Override
    public void delete(int id) {
        Optional<ThuongHieu> brandOpt = brandRepository.findById(id);
        if (brandOpt.isPresent()) {
            ThuongHieu thuongHieu = brandOpt.get();
            if (StringUtils.hasText(thuongHieu.getHinhAnh())) {
                storageService.deleteFile(thuongHieu.getHinhAnh());
            }
            brandRepository.deleteById(id);
        }
    }
}
