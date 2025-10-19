package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.AppliedProvince;
import nhom17.OneShop.entity.AppliedProvinceId;
import nhom17.OneShop.entity.ShippingCarrier;
import nhom17.OneShop.entity.ShippingFee;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.exception.NotFoundException;
import nhom17.OneShop.repository.ShippingCarrierRepository;
import nhom17.OneShop.repository.ShippingFeeRepository;
import nhom17.OneShop.request.ShippingFeeRequest;
import nhom17.OneShop.service.ShippingFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class ShippingFeeServiceImpl implements ShippingFeeService {

    @Autowired
    private ShippingFeeRepository shippingFeeRepository;
    @Autowired
    private ShippingCarrierRepository shippingCarrierRepository;

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
        shippingFeeRepository.save(entity);
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
                        if (!fee.getTenGoiCuoc().equalsIgnoreCase(request.getTenGoiCuoc().trim())) {
                            throw new IllegalArgumentException("Tỉnh '" + province.getId().getTenTinhThanh() + "' đã thuộc gói phí '" + fee.getTenGoiCuoc() + "'."
                            );
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

        entity.setTenGoiCuoc(request.getTenGoiCuoc());
        entity.setNhaVanChuyen(provider);
        entity.setPhuongThucVanChuyen(request.getPhuongThucVanChuyen());
        entity.setChiPhi(request.getChiPhi());
        entity.setNgayGiaoSomNhat(request.getNgayGiaoSomNhat());
        entity.setNgayGiaoMuonNhat(request.getNgayGiaoMuonNhat());
        entity.setDonViThoiGian(request.getDonViThoiGian());

        updateAppliedProvinces(request, entity);
    }

    private void updateAppliedProvinces(ShippingFeeRequest request, ShippingFee entity) {
        if (entity.getCacTinhApDung() == null) {
            entity.setCacTinhApDung(new HashSet<>());
        }
        entity.getCacTinhApDung().clear();
        shippingFeeRepository.flush();

        for (String tenTinhThanh : request.getCacTinhApDung()) {
            AppliedProvinceId appliedId = new AppliedProvinceId(entity.getMaChiPhiVC(), tenTinhThanh);
            AppliedProvince appliedProvince = new AppliedProvince();
            appliedProvince.setId(appliedId);
            appliedProvince.setPhiVanChuyen(entity);
            entity.getCacTinhApDung().add(appliedProvince);
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
}
