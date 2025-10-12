package nhom17.OneShop.repository;

import nhom17.OneShop.entity.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportRepository extends JpaRepository<Import, Integer>, JpaSpecificationExecutor<Import> {
    boolean existsByNhaCungCap_MaNCC(Integer supplierId);
}
