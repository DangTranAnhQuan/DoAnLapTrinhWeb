package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.*;
import nhom17.OneShop.repository.*;
import nhom17.OneShop.request.ImportDetailRequest;
import nhom17.OneShop.request.ImportRequest;
import nhom17.OneShop.service.ImportService;
import nhom17.OneShop.specification.ImportSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private ImportRepository importRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ImportDetailRepository importDetailRepository;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public Page<Import> findAll(String keyword, Integer supplierId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("ngayTao").descending());

        Specification<Import> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(keyword)) {
            try {
                Integer id = Integer.parseInt(keyword);
                spec = spec.and(ImportSpecification.hasId(id));
            } catch (NumberFormatException e) {
                return Page.empty(pageable);
            }
        }

        if (supplierId != null) {
            spec = spec.and(ImportSpecification.hasSupplier(supplierId));
        }

        return importRepository.findAll(spec, pageable);
    }

    @Override
    public Import findById(int id) {
        return importRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void save(ImportRequest importRequest) {
        Supplier supplier = supplierRepository.findById(importRequest.getMaNCC())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà cung cấp"));

        Import phieuNhap;
        List<ImportDetail> newChiTietList = new ArrayList<>();

        // Xử lý Sửa
        if (importRequest.getMaPhieuNhap() != null) {
            phieuNhap = importRepository.findById(importRequest.getMaPhieuNhap())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập"));

            // Hoàn trả lại số lượng tồn kho từ các chi tiết cũ
            for (ImportDetail oldDetail : phieuNhap.getImportDetailList()) {
                Inventory inventory = inventoryRepository.findById(oldDetail.getSanPham().getMaSanPham()).orElse(null);
                if (inventory != null) {
                    inventory.setSoLuongTon(inventory.getSoLuongTon() - oldDetail.getSoLuong());
                    inventoryRepository.save(inventory);
                }
            }

            // ✅ Xóa các chi tiết cũ khỏi collection trong bộ nhớ
            // orphanRemoval=true sẽ tự động xóa chúng khỏi CSDL khi lưu phiếu nhập
            phieuNhap.getImportDetailList().clear();

        } else { // Xử lý Thêm mới
            phieuNhap = new Import();
        }

        phieuNhap.setNhaCungCap(supplier);

        // Tạo danh sách chi tiết mới
        for (ImportDetailRequest detailRequest : importRequest.getChiTietPhieuNhapList()) {
            Product sanPham = productRepository.findById(detailRequest.getMaSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            ImportDetail chiTiet = new ImportDetail();
            chiTiet.setPhieuNhap(phieuNhap); // Quan trọng: Liên kết với đối tượng cha
            chiTiet.setSanPham(sanPham);
            chiTiet.setSoLuong(detailRequest.getSoLuong());
            chiTiet.setGiaNhap(detailRequest.getGiaNhap());
            newChiTietList.add(chiTiet);

            // Cập nhật số lượng tồn kho
            Inventory inventory = inventoryRepository.findById(sanPham.getMaSanPham()).orElse(new Inventory());
            if (inventory.getSanPham() == null) {
                inventory.setSanPham(sanPham);
                inventory.setMaSanPham(sanPham.getMaSanPham());
            }
            int currentStock = inventory.getSoLuongTon() != null ? inventory.getSoLuongTon() : 0;
            inventory.setSoLuongTon(currentStock + detailRequest.getSoLuong());
            inventory.setNgayNhapGanNhat(LocalDateTime.now());
            inventoryRepository.save(inventory);
        }

        // ✅ Thêm danh sách chi tiết mới vào collection
        phieuNhap.getImportDetailList().addAll(newChiTietList);

        // ✅ Lưu đối tượng cha, cascade sẽ tự động lưu các đối tượng con
        importRepository.save(phieuNhap);
    }

    @Override
    @Transactional
    public void delete(int id) {
        Import phieuNhap = importRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập"));
        for (ImportDetail detail : phieuNhap.getImportDetailList()) {
            Inventory inventory = inventoryRepository.findById(detail.getSanPham().getMaSanPham()).orElse(null);
            if (inventory != null) {
                inventory.setSoLuongTon(inventory.getSoLuongTon() - detail.getSoLuong());
                inventoryRepository.save(inventory);
            }
        }
        importRepository.delete(phieuNhap);
    }
}
