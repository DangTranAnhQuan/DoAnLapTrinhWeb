package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByNguoiDung_MaNguoiDung(Integer userId);
}