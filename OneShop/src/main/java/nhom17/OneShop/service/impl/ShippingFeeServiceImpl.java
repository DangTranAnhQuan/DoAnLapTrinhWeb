//package nhom17.OneShop.service.impl;
//
//import nhom17.OneShop.dto.ShippingOptionDTO;
//import nhom17.OneShop.entity.AppliedProvince;
//import nhom17.OneShop.entity.AppliedProvinceId;
//import nhom17.OneShop.entity.ShippingCarrier;
//import nhom17.OneShop.entity.ShippingFee;
//import nhom17.OneShop.exception.DuplicateRecordException;
//import nhom17.OneShop.exception.NotFoundException;
//import nhom17.OneShop.repository.ShippingCarrierRepository;
//import nhom17.OneShop.repository.ShippingFeeRepository;
//import nhom17.OneShop.request.ShippingFeeRequest;
//import nhom17.OneShop.service.CartService;
//import nhom17.OneShop.service.ShippingFeeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.HashSet;
//import java.util.List;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.*;
//import java.util.Comparator;
//import java.util.stream.Collectors;
//
//@Service
//public class ShippingFeeServiceImpl implements ShippingFeeService {
//
//    @Autowired private ShippingFeeRepository shippingFeeRepository;
//    @Autowired private ShippingCarrierRepository shippingCarrierRepository;
//    @Autowired private CartService cartService;
//
//    // --- CÁC PHƯƠNG THỨC QUẢN LÝ (ADMIN) ---
//
//    @Override
//    public List<ShippingFee> findAllByProvider(int providerId) {
//        return shippingFeeRepository.findByNhaVanChuyen_MaNVCOrderByMaChiPhiVCDesc(providerId);
//    }
//
//    @Override
//    public ShippingFee findById(int id) {
//        return shippingFeeRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy gói phí vận chuyển với ID: " + id));
//    }
//
//    @Override
//    @Transactional
//    public void save(ShippingFeeRequest request) {
//        validateRequest(request);
//        ShippingFee entity = prepareEntity(request);
//        mapToEntity(request, entity);
//        // Logic save đã nằm trong updateAppliedProvinces
//    }
//
//    private void validateRequest(ShippingFeeRequest request) {
//        Integer feeId = request.getMaChiPhiVC();
//        Integer carrierId = request.getMaNVC();
//        String method = request.getPhuongThucVanChuyen();
//
//        if (request.getNgayGiaoMuonNhat() <= request.getNgayGiaoSomNhat()) {
//            throw new IllegalArgumentException("Ngày giao muộn nhất không được nhỏ hơn ngày giao sớm nhất.");
//        }
//
//        if (feeId == null) {
//            if (shippingFeeRepository.existsByPhuongThucVanChuyenIgnoreCaseAndTenGoiCuocIgnoreCaseAndNhaVanChuyen_MaNVC(
//                    method, request.getTenGoiCuoc().trim(), carrierId)) {
//                throw new DuplicateRecordException("Phương thức vận chuyển '" + method + "' đã tồn tại trong gói cước '" + request.getTenGoiCuoc() + "' của nhà vận chuyển này.");
//            }
//        } else {
//            if (shippingFeeRepository.existsByPhuongThucVanChuyenIgnoreCaseAndTenGoiCuocIgnoreCaseAndNhaVanChuyen_MaNVCAndMaChiPhiVCNot(
//                    method, request.getTenGoiCuoc().trim(), carrierId, feeId)) {
//                throw new DuplicateRecordException("Phương thức vận chuyển '" + method + "' đã bị trùng trong gói cước '" + request.getTenGoiCuoc() + "' của nhà vận chuyển này.");
//            }
//        }
//
//
//        if (request.getCacTinhApDung() != null && !request.getCacTinhApDung().isEmpty()) {
//            List<ShippingFee> existingFees = shippingFeeRepository.findByNhaVanChuyen_MaNVCOrderByMaChiPhiVCDesc(carrierId);
//
//            for (ShippingFee fee : existingFees) {
//                if (feeId != null && fee.getMaChiPhiVC().equals(feeId)) continue;
//
//                for (AppliedProvince province : fee.getCacTinhApDung()) {
//                    if (request.getCacTinhApDung().contains(province.getId().getTenTinhThanh())) {
//                        if (fee.getPhuongThucVanChuyen().equalsIgnoreCase(request.getPhuongThucVanChuyen().trim())) {
//                            throw new IllegalArgumentException("Tỉnh '" + province.getId().getTenTinhThanh() + "' đã được áp dụng cho phương thức '" + fee.getPhuongThucVanChuyen() + "' trong gói '" + fee.getTenGoiCuoc() + "'.");
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//
//    private ShippingFee prepareEntity(ShippingFeeRequest request) {
//        if (request.getMaChiPhiVC() != null) {
//            return this.findById(request.getMaChiPhiVC());
//        }
//        return new ShippingFee();
//    }
//
//    private void mapToEntity(ShippingFeeRequest request, ShippingFee entity) {
//        ShippingCarrier provider = shippingCarrierRepository.findById(request.getMaNVC())
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà vận chuyển với ID: " + request.getMaNVC()));
//
//        entity.setTenGoiCuoc(request.getTenGoiCuoc().trim());
//        entity.setNhaVanChuyen(provider);
//        entity.setPhuongThucVanChuyen(request.getPhuongThucVanChuyen());
//        entity.setChiPhi(request.getChiPhi());
//        entity.setNgayGiaoSomNhat(request.getNgayGiaoSomNhat());
//        entity.setNgayGiaoMuonNhat(request.getNgayGiaoMuonNhat());
//        entity.setDonViThoiGian(request.getDonViThoiGian());
//
//        updateAppliedProvinces(request, entity);
//    }
//
//    private void updateAppliedProvinces(ShippingFeeRequest request, ShippingFee entity) {
//        boolean isNew = entity.getMaChiPhiVC() == null;
//        if (isNew) {
//            shippingFeeRepository.saveAndFlush(entity);
//        }
//
//        if (entity.getCacTinhApDung() == null) {
//            entity.setCacTinhApDung(new HashSet<>());
//        }
//
//        Set<AppliedProvince> newProvinces = new HashSet<>();
//        if (request.getCacTinhApDung() != null) {
//            for (String tenTinhThanh : request.getCacTinhApDung()) {
//                AppliedProvinceId appliedId = new AppliedProvinceId(entity.getMaChiPhiVC(), tenTinhThanh);
//                AppliedProvince appliedProvince = new AppliedProvince(appliedId, entity);
//                newProvinces.add(appliedProvince);
//            }
//        }
//
//        entity.getCacTinhApDung().clear();
//        entity.getCacTinhApDung().addAll(newProvinces);
//
//        if (!isNew) {
//            shippingFeeRepository.save(entity);
//        }
//    }
//
//    @Override
//    @Transactional
//    public void delete(int id) {
//        if (!shippingFeeRepository.existsById(id)) {
//            throw new NotFoundException("Không tìm thấy gói phí vận chuyển để xóa.");
//        }
//        shippingFeeRepository.deleteById(id);
//    }
//
//    // --- CÁC PHƯƠNG THỨC CHO CHECKOUT (LOGIC CỦA BẠN) ---
//
//    @Override
//    public Optional<ShippingOptionDTO> findCheapestShippingOption(String province, BigDecimal subtotal) {
//        // Phương thức này giờ sẽ gọi logic mới và lấy phần tử đầu tiên
//        List<ShippingOptionDTO> options = findAvailableShippingOptions(province);
//        if (options.isEmpty()) {
//            return Optional.empty();
//        }
//        return Optional.of(options.get(0)); // Trả về gói rẻ nhất sau khi đã lọc
//    }
//
//    // <<< PHƯƠNG THỨC ĐÃ SỬA THEO YÊU CẦU MỚI >>>
//    @Override
//    public List<ShippingOptionDTO> findAvailableShippingOptions(String province) {
//        // 1. Lấy tất cả các gói cước áp dụng cho tỉnh/thành
//        List<ShippingFee> applicableFees = shippingFeeRepository.findShippingFeesByProvince(province);
//
//        // 2. Lấy subtotal để tính giảm giá
//        BigDecimal subtotal = cartService.getSubtotal();
//
//        // 3. Nhóm theo Phương thức vận chuyển (Tiết Kiệm, Nhanh, v.v.)
//        //    và chỉ chọn ra gói rẻ nhất (ChiPhi gốc) từ mỗi nhóm
//        Map<String, Optional<ShippingFee>> cheapestFeesByMethod = applicableFees.stream()
//                .collect(Collectors.groupingBy(
//                        ShippingFee::getPhuongThucVanChuyen, // Nhóm theo "Tiết Kiệm", "Nhanh", "Tiêu Chuẩn"
//                        Collectors.minBy(Comparator.comparing(ShippingFee::getChiPhi)) // Chỉ lấy gói rẻ nhất trong nhóm
//                ));
//
//        // 4. Chuyển đổi Map<String, Optional<ShippingFee>> thành List<ShippingOptionDTO>
//        List<ShippingOptionDTO> options = cheapestFeesByMethod.values().stream()
//                .filter(Optional::isPresent) // Lọc bỏ các nhóm không có giá trị
//                .map(Optional::get) // Lấy ShippingFee từ Optional
//                .map(fee -> {
//                    // Chuyển đổi sang DTO
//                    ShippingOptionDTO dto = convertToDTO(fee); // Gọi hàm helper đã sửa
//                    // Tính toán chi phí cuối cùng (đã giảm giá/freeship)
//                    BigDecimal finalCost = calculateFinalShippingCost(subtotal, fee.getChiPhi());
//                    dto.setChiPhi(finalCost); // Cập nhật chi phí cuối cùng
//                    return dto;
//                })
//                .sorted(Comparator.comparing(ShippingOptionDTO::getChiPhi)) // Sắp xếp theo giá cuối cùng tăng dần
//                .collect(Collectors.toList());
//
//        return options;
//    }
//
//    // Hàm helper tính toán phí ship (tách logic)
//    private BigDecimal calculateFinalShippingCost(BigDecimal subtotal, BigDecimal originalCost) {
//        BigDecimal oneMillion = new BigDecimal("1000000");
//        BigDecimal fiveHundredThousand = new BigDecimal("500000");
//
//        if (subtotal.compareTo(oneMillion) >= 0) {
//            return BigDecimal.ZERO;
//        } else if (subtotal.compareTo(fiveHundredThousand) >= 0) {
//            return originalCost.multiply(new BigDecimal("0.5")).setScale(0, RoundingMode.HALF_UP);
//        } else {
//            return originalCost;
//        }
//    }
//
//    // <<< HÀM HELPER ĐÃ SỬA ĐỂ KHỚP VỚI ShippingOptionDTO.java CỦA BẠN >>>
//    private ShippingOptionDTO convertToDTO(ShippingFee fee) {
//        ShippingOptionDTO dto = new ShippingOptionDTO();
//
//        // Gán vào các trường TỒN TẠI trong DTO của bạn
//        dto.setMaChiPhiVC(fee.getMaChiPhiVC());
//        dto.setChiPhi(fee.getChiPhi()); // Phí gốc (sẽ bị ghi đè bên ngoài)
//        dto.setNgayGiaoSomNhat(fee.getNgayGiaoSomNhat());
//        dto.setNgayGiaoMuonNhat(fee.getNgayGiaoMuonNhat());
//        dto.setDonViThoiGian(fee.getDonViThoiGian());
//
//        String carrierName = (fee.getNhaVanChuyen() != null) ? fee.getNhaVanChuyen().getTenNVC() : "N/A";
//        dto.setTenNVC(carrierName);
//
//        // Gán tên kết hợp (VD: "GHTK - Tiết Kiệm") vào trường 'tenGoiCuoc'
//        dto.setTenGoiCuoc(carrierName + " - " + fee.getPhuongThucVanChuyen());
//
//        return dto;
//    }
//}
package nhom17.OneShop.service.impl;

import nhom17.OneShop.dto.ShippingOptionDTO;
import nhom17.OneShop.entity.AppliedProvince;
import nhom17.OneShop.entity.AppliedProvinceId;
import nhom17.OneShop.entity.ShippingCarrier;
import nhom17.OneShop.entity.ShippingFee;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.exception.NotFoundException;
import nhom17.OneShop.repository.ShippingCarrierRepository;
import nhom17.OneShop.repository.ShippingFeeRepository;
import nhom17.OneShop.request.ShippingFeeRequest;
import nhom17.OneShop.service.CartService;
import nhom17.OneShop.service.ShippingFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class ShippingFeeServiceImpl implements ShippingFeeService {

    @Autowired private ShippingFeeRepository shippingFeeRepository;
    @Autowired private ShippingCarrierRepository shippingCarrierRepository;
    @Autowired private CartService cartService;

    // --- CÁC PHƯƠNG THỨC QUẢN LÝ (ADMIN) - GIỮ NGUYÊN ---
    @Override
    public List<ShippingFee> findAllByProvider(int providerId) {
        return shippingFeeRepository.findByNhaVanChuyen_MaNVCOrderByMaChiPhiVCDesc(providerId);
    }

    @Override
    public ShippingFee findById(int id) {
        return shippingFeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy gói phí vận chuyển với ID: " + id));
    }

    @Override
    @Transactional
    public void save(ShippingFeeRequest request) {
        validateRequest(request);
        ShippingFee entity = prepareEntity(request);
        mapToEntity(request, entity);
        // Logic save đã nằm trong updateAppliedProvinces
    }

    private void validateRequest(ShippingFeeRequest request) {
        Integer feeId = request.getMaChiPhiVC();
        Integer carrierId = request.getMaNVC();
        String method = request.getPhuongThucVanChuyen();

        if (request.getNgayGiaoMuonNhat() <= request.getNgayGiaoSomNhat()) {
            throw new IllegalArgumentException("Ngày giao muộn nhất không được nhỏ hơn ngày giao sớm nhất.");
        }

        if (feeId == null) {
            if (shippingFeeRepository.existsByPhuongThucVanChuyenIgnoreCaseAndTenGoiCuocIgnoreCaseAndNhaVanChuyen_MaNVC(
                    method, request.getTenGoiCuoc().trim(), carrierId)) {
                throw new DuplicateRecordException("Phương thức vận chuyển '" + method + "' đã tồn tại trong gói cước '" + request.getTenGoiCuoc() + "' của nhà vận chuyển này.");
            }
        } else {
            if (shippingFeeRepository.existsByPhuongThucVanChuyenIgnoreCaseAndTenGoiCuocIgnoreCaseAndNhaVanChuyen_MaNVCAndMaChiPhiVCNot(
                    method, request.getTenGoiCuoc().trim(), carrierId, feeId)) {
                throw new DuplicateRecordException("Phương thức vận chuyển '" + method + "' đã bị trùng trong gói cước '" + request.getTenGoiCuoc() + "' của nhà vận chuyển này.");
            }
        }


        if (request.getCacTinhApDung() != null && !request.getCacTinhApDung().isEmpty()) {
            List<ShippingFee> existingFees = shippingFeeRepository.findByNhaVanChuyen_MaNVCOrderByMaChiPhiVCDesc(carrierId);

            for (ShippingFee fee : existingFees) {
                if (feeId != null && fee.getMaChiPhiVC().equals(feeId)) continue;

                for (AppliedProvince province : fee.getCacTinhApDung()) {
                    if (request.getCacTinhApDung().contains(province.getId().getTenTinhThanh())) {
                        if (fee.getPhuongThucVanChuyen().equalsIgnoreCase(request.getPhuongThucVanChuyen().trim())) {
                            throw new IllegalArgumentException("Tỉnh '" + province.getId().getTenTinhThanh() + "' đã được áp dụng cho phương thức '" + fee.getPhuongThucVanChuyen() + "' trong gói '" + fee.getTenGoiCuoc() + "'.");
                        }
                    }
                }
            }
        }
    }


    private ShippingFee prepareEntity(ShippingFeeRequest request) {
        if (request.getMaChiPhiVC() != null) {
            return this.findById(request.getMaChiPhiVC());
        }
        return new ShippingFee();
    }

    private void mapToEntity(ShippingFeeRequest request, ShippingFee entity) {
        ShippingCarrier provider = shippingCarrierRepository.findById(request.getMaNVC())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà vận chuyển với ID: " + request.getMaNVC()));

        entity.setTenGoiCuoc(request.getTenGoiCuoc().trim());
        entity.setNhaVanChuyen(provider);
        entity.setPhuongThucVanChuyen(request.getPhuongThucVanChuyen());
        entity.setChiPhi(request.getChiPhi());
        entity.setNgayGiaoSomNhat(request.getNgayGiaoSomNhat());
        entity.setNgayGiaoMuonNhat(request.getNgayGiaoMuonNhat());
        entity.setDonViThoiGian(request.getDonViThoiGian());

        updateAppliedProvinces(request, entity);
    }

    private void updateAppliedProvinces(ShippingFeeRequest request, ShippingFee entity) {
        boolean isNew = entity.getMaChiPhiVC() == null;
        if (isNew) {
            shippingFeeRepository.saveAndFlush(entity);
        }

        if (entity.getCacTinhApDung() == null) {
            entity.setCacTinhApDung(new HashSet<>());
        }

        Set<AppliedProvince> newProvinces = new HashSet<>();
        if (request.getCacTinhApDung() != null) {
            for (String tenTinhThanh : request.getCacTinhApDung()) {
                AppliedProvinceId appliedId = new AppliedProvinceId(entity.getMaChiPhiVC(), tenTinhThanh);
                AppliedProvince appliedProvince = new AppliedProvince(appliedId, entity);
                newProvinces.add(appliedProvince);
            }
        }

        entity.getCacTinhApDung().clear();
        entity.getCacTinhApDung().addAll(newProvinces);

        if (!isNew) {
            shippingFeeRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void delete(int id) {
        if (!shippingFeeRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy gói phí vận chuyển để xóa.");
        }
        shippingFeeRepository.deleteById(id);
    }

    // --- CÁC PHƯƠNG THỨC CHO CHECKOUT ---

    // Hàm helper tính toán phí ship (tách logic)
    private BigDecimal calculateFinalShippingCost(BigDecimal subtotal, BigDecimal originalCost) {
        BigDecimal oneMillion = new BigDecimal("1000000");
        BigDecimal fiveHundredThousand = new BigDecimal("500000");

        if (subtotal.compareTo(oneMillion) >= 0) {
            return BigDecimal.ZERO;
        } else if (subtotal.compareTo(fiveHundredThousand) >= 0) {
            return originalCost.multiply(new BigDecimal("0.5")).setScale(0, RoundingMode.HALF_UP);
        } else {
            return originalCost;
        }
    }

    private ShippingOptionDTO convertToDTO(ShippingFee fee) {
        ShippingOptionDTO dto = new ShippingOptionDTO();

        dto.setMaChiPhiVC(fee.getMaChiPhiVC());
        dto.setChiPhi(fee.getChiPhi()); // Phí gốc (sẽ bị ghi đè bên ngoài)
        dto.setNgayGiaoSomNhat(fee.getNgayGiaoSomNhat());
        dto.setNgayGiaoMuonNhat(fee.getNgayGiaoMuonNhat());
        dto.setDonViThoiGian(fee.getDonViThoiGian());

        String carrierName = (fee.getNhaVanChuyen() != null) ? fee.getNhaVanChuyen().getTenNVC() : "N/A";
        dto.setTenNVC(carrierName); // Gán tên nhà vận chuyển

        // Gán TÊN PHƯƠNG THỨC (Tiết Kiệm, Nhanh) vào 'tenGoiCuoc'
        dto.setTenGoiCuoc(fee.getPhuongThucVanChuyen());

        return dto;
    }

    @Override
    public Optional<ShippingOptionDTO> findCheapestShippingOption(String province, BigDecimal subtotal) {
        List<ShippingOptionDTO> options = findAvailableShippingOptions(province);
        if (options.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(options.get(0)); // Trả về gói rẻ nhất sau khi đã lọc
    }

    @Override
    public List<ShippingOptionDTO> findAvailableShippingOptions(String province) {
        List<ShippingFee> applicableFees = shippingFeeRepository.findShippingFeesByProvince(province);
        BigDecimal subtotal = cartService.getSubtotal();

        // Nhóm theo Phương thức vận chuyển và chỉ chọn gói rẻ nhất
        Map<String, Optional<ShippingFee>> cheapestFeesByMethod = applicableFees.stream()
                .collect(Collectors.groupingBy(
                        ShippingFee::getPhuongThucVanChuyen,
                        Collectors.minBy(Comparator.comparing(ShippingFee::getChiPhi))
                ));

        // Chuyển đổi Map sang List DTO và tính phí cuối cùng
        List<ShippingOptionDTO> options = cheapestFeesByMethod.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(fee -> {
                    ShippingOptionDTO dto = convertToDTO(fee); // Gọi hàm helper đã sửa
                    BigDecimal finalCost = calculateFinalShippingCost(subtotal, fee.getChiPhi());
                    dto.setChiPhi(finalCost); // Cập nhật chi phí cuối cùng
                    return dto;
                })
                .sorted(Comparator.comparing(ShippingOptionDTO::getChiPhi)) // Sắp xếp theo giá
                .collect(Collectors.toList());

        return options;
    }

    @Override
    public List<String> findDistinctShippingMethods() {
        return shippingFeeRepository.findDistinctPhuongThucVanChuyen();
    }
}