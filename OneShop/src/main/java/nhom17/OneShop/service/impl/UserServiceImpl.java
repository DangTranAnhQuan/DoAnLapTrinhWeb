package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.entity.Role;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.repository.RoleRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.request.UserRequest;
import nhom17.OneShop.service.UserService;
import nhom17.OneShop.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private MembershipTierRepository membershipTierRepository;

    @Override
    public Page<User> findAll(String keyword, Integer roleId, Integer tierId, Integer status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maNguoiDung").descending());

        Specification<User> spec = (root, query, cb) -> cb.conjunction();
        if (StringUtils.hasText(keyword)) {
            spec = spec.and(UserSpecification.hasUsername(keyword));
        }
        if (roleId != null) {
            spec = spec.and(UserSpecification.hasRole(roleId));
        }
        if (tierId != null) {
            spec = spec.and(UserSpecification.hasMembershipTier(tierId));
        }
        if (status != null) {
            spec = spec.and(UserSpecification.hasStatus(status));
        }

        return userRepository.findAll(spec, pageable);
    }

    @Override
    public User findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void save(UserRequest userRequest) {
        Optional<User> existingEmail = userRepository.findByEmail(userRequest.getEmail());
        if (existingEmail.isPresent() && (userRequest.getMaNguoiDung() == null || !existingEmail.get().getMaNguoiDung().equals(userRequest.getMaNguoiDung()))) {
            throw new DuplicateRecordException("Email '" + userRequest.getEmail() + "' đã được sử dụng.");
        }

        Optional<User> existingUsername = userRepository.findByTenDangNhapIgnoreCase(userRequest.getTenDangNhap());
        if (existingUsername.isPresent() && (userRequest.getMaNguoiDung() == null || !existingUsername.get().getMaNguoiDung().equals(userRequest.getMaNguoiDung()))) {
            throw new DuplicateRecordException("Tên đăng nhập '" + userRequest.getTenDangNhap() + "' đã tồn tại.");
        }

        User user;
        if (userRequest.getMaNguoiDung() != null) { // Sửa
            user = userRepository.findById(userRequest.getMaNguoiDung())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        } else { // Thêm mới
            user = new User();
        }

        user.setHoTen(userRequest.getHoTen());
        user.setEmail(userRequest.getEmail());
        user.setTenDangNhap(userRequest.getTenDangNhap());
        user.setSoDienThoai(userRequest.getSoDienThoai());
        user.setTrangThai(userRequest.getTrangThai());

        // Chỉ cập nhật mật khẩu nếu nó được nhập vào
        // LƯU Ý QUAN TRỌNG: TRONG THỰC TẾ BẠN PHẢI MÃ HÓA MẬT KHẨU NÀY
        if (StringUtils.hasText(userRequest.getMatKhau())) {
            user.setMatKhau(userRequest.getMatKhau());
        }

        Role role = roleRepository.findById(userRequest.getMaVaiTro())
                .orElseThrow(() -> new RuntimeException("Vai trò không hợp lệ"));
        user.setVaiTro(role);

        if (userRequest.getMaHangThanhVien() != null) {
            MembershipTier tier = membershipTierRepository.findById(userRequest.getMaHangThanhVien())
                    .orElseThrow(() -> new RuntimeException("Hạng thành viên không hợp lệ"));
            user.setHangThanhVien(tier);
        } else {
            user.setHangThanhVien(null);
        }

        userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
