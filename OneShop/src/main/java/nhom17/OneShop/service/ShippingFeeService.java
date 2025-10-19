package nhom17.OneShop.service;

import nhom17.OneShop.entity.ShippingCarrier;
import nhom17.OneShop.entity.ShippingFee;
import nhom17.OneShop.request.ShippingFeeRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ShippingFeeService {
    List<ShippingFee> findAllByProvider(int providerId);
    ShippingFee findById(int id);
    void save(ShippingFeeRequest request);
    void delete(int id);
}
