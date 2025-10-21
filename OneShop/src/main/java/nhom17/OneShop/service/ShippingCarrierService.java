package nhom17.OneShop.service;

import nhom17.OneShop.entity.ShippingCarrier;
import nhom17.OneShop.request.ShippingCarrierRequest;
import org.springframework.data.domain.Page;

public interface ShippingCarrierService {
    Page<ShippingCarrier> search(String keyword, int page, int size);

    ShippingCarrier findById(int id);

    void save(ShippingCarrierRequest request);
    void delete(int id);
}
