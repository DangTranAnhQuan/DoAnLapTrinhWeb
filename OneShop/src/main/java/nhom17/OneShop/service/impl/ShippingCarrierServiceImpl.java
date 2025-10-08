package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.ShippingCarrier;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.repository.ShippingCarrierRepository;
import nhom17.OneShop.request.ShippingCarrierRequest;
import nhom17.OneShop.service.ShippingCarrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class ShippingCarrierServiceImpl implements ShippingCarrierService {

    @Autowired
    private ShippingCarrierRepository shippingCarrierRepository;

    @Override
    public Page<ShippingCarrier> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maNVC").ascending());
        if (StringUtils.hasText(keyword)) {
            return shippingCarrierRepository.findByTenNVCContainingIgnoreCase(keyword, pageable);
        }
        return shippingCarrierRepository.findAll(pageable);
    }

    @Override
    public void save(ShippingCarrierRequest request) {
        Optional<ShippingCarrier> existing = shippingCarrierRepository.findByTenNVCIgnoreCase(request.getTenNVC());
        if (existing.isPresent() && (request.getMaNVC() == null || !existing.get().getMaNVC().equals(request.getMaNVC()))) {
            throw new DuplicateRecordException("Tên nhà vận chuyển '" + request.getTenNVC() + "' đã tồn tại.");
        }

        ShippingCarrier carrier = new ShippingCarrier();
        if (request.getMaNVC() != null) {
            carrier = shippingCarrierRepository.findById(request.getMaNVC())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà vận chuyển"));
        }
        carrier.setTenNVC(request.getTenNVC());
        carrier.setSoDienThoai(request.getSoDienThoai());
        carrier.setWebsite(request.getWebsite());
        shippingCarrierRepository.save(carrier);
    }

    @Override
    public void delete(int id) {
        shippingCarrierRepository.deleteById(id);
    }
}
