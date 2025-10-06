package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.request.MembershipTierRequest;
import nhom17.OneShop.service.MembershipTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
