package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.ShippingCarrier;
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
