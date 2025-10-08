package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.request.MembershipTierRequest;
import nhom17.OneShop.service.MembershipTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipTierServiceImpl implements MembershipTierService {

    @Autowired
    private MembershipTierRepository membershipTierRepository;

    @Override
    public List<MembershipTier> findAllSorted() {
        return membershipTierRepository.findAll(Sort.by("diemToiThieu").ascending());
    }

    @Override
    public void save(MembershipTierRequest request) {
        Optional<MembershipTier> existingName = membershipTierRepository.findByTenHangIgnoreCase(request.getTenHang());
        if (existingName.isPresent() && (request.getMaHangThanhVien() == null || !existingName.get().getMaHangThanhVien().equals(request.getMaHangThanhVien()))) {
            throw new DuplicateRecordException("Tên hạng '" + request.getTenHang() + "' đã tồn tại.");
        }

        Optional<MembershipTier> existingPoints = membershipTierRepository.findByDiemToiThieu(request.getDiemToiThieu());
        if (existingPoints.isPresent() && (request.getMaHangThanhVien() == null || !existingPoints.get().getMaHangThanhVien().equals(request.getMaHangThanhVien()))) {
            throw new DuplicateRecordException("Điểm tối thiểu '" + request.getDiemToiThieu() + "' đã được sử dụng cho hạng khác.");
        }

        MembershipTier tier = new MembershipTier();
        if (request.getMaHangThanhVien() != null) {
            tier = membershipTierRepository.findById(request.getMaHangThanhVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng thành viên"));
        }
        tier.setTenHang(request.getTenHang());
        tier.setDiemToiThieu(request.getDiemToiThieu());
        tier.setPhanTramGiamGia(request.getPhanTramGiamGia());
        membershipTierRepository.save(tier);
    }

    @Override
    public void delete(int id) {
        membershipTierRepository.deleteById(id);
    }
}
