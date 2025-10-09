package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.Voucher;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.repository.VoucherRepository;
import nhom17.OneShop.request.VoucherRequest;
import nhom17.OneShop.service.VoucherService;
import nhom17.OneShop.specification.VoucherSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Override
    public Page<Voucher> findAll(String keyword, Integer status, Integer type, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayTao").descending());
        return voucherRepository.findAll(VoucherSpecification.filterByCriteria(keyword, status, type), pageable);
    }

    @Override
    public Voucher findById(String id) {
        return voucherRepository.findById(id).orElse(null);
    }

    @Override
    public void save(VoucherRequest request) {
        // 1. Kiểm tra ngày hợp lệ
        if (request.getBatDauLuc() != null && request.getKetThucLuc() != null &&
                request.getKetThucLuc().isBefore(request.getBatDauLuc())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }

        String voucherCode = request.getMaKhuyenMai().toUpperCase();

        Optional<Voucher> existingVoucherOptional = voucherRepository.findById(voucherCode);

        Voucher voucher;

        if (existingVoucherOptional.isPresent()) {
            voucher = existingVoucherOptional.get();
        } else {
            voucher = new Voucher();
            voucher.setMaKhuyenMai(voucherCode);
            voucher.setNgayTao(LocalDateTime.now());
        }

        voucher.setTenChienDich(request.getTenChienDich());
        voucher.setKieuApDung(request.getKieuApDung());
        voucher.setGiaTri(request.getGiaTri());
        voucher.setBatDauLuc(request.getBatDauLuc());
        voucher.setKetThucLuc(request.getKetThucLuc());
        voucher.setTongTienToiThieu(request.getTongTienToiThieu());
        voucher.setGiamToiDa(request.getGiamToiDa());
        voucher.setGioiHanTongSoLan(request.getGioiHanTongSoLan());
        voucher.setGioiHanMoiNguoi(request.getGioiHanMoiNguoi());
        voucher.setTrangThai(request.getTrangThai());

        voucherRepository.save(voucher);
    }

    @Override
    public void delete(String id) {
        voucherRepository.deleteById(id);
    }
}
