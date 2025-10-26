package nhom17.OneShop.service;

import nhom17.OneShop.dto.ShippingOptionDTO; // Import DTO
import nhom17.OneShop.entity.ShippingFee;
import nhom17.OneShop.request.ShippingFeeRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ShippingFeeService {
    List<ShippingFee> findAllByProvider(int providerId);
    List<String> findDistinctShippingMethods();
    ShippingFee findById(int id);
    void save(ShippingFeeRequest request);
    void delete(int id);
    Optional<ShippingOptionDTO> findCheapestShippingOption(String province, BigDecimal subtotal);
}