package nhom17.OneShop.repository;

import nhom17.OneShop.entity.ImportDetail;
import nhom17.OneShop.entity.ImportDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportDetailRepository extends JpaRepository<ImportDetail, ImportDetailId> {
    @Query("SELECT id FROM ImportDetail id " +
            "JOIN FETCH id.phieuNhap pn " +
            "JOIN FETCH pn.nhaCungCap " +
            "WHERE id.sanPham.maSanPham = :productId " +
            "ORDER BY pn.ngayTao DESC")
    List<ImportDetail> findHistoryByProductId(Integer productId);
}
