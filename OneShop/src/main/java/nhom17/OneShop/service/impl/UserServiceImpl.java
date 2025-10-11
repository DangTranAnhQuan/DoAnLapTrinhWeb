package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.entity.Role;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.exception.NotFoundException;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.repository.RoleRepository;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.request.UserRequest;
import nhom17.OneShop.service.StorageService;
import nhom17.OneShop.service.UserService;
import nhom17.OneShop.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired private
    RoleRepository roleRepository;
    @Autowired private
    MembershipTierRepository membershipTierRepository;
    @Autowired
    private StorageService storageService;

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
        // Phần kiểm tra email và username giữ nguyên
        Optional<User> existingEmail = userRepository.findByEmail(userRequest.getEmail());
        if (existingEmail.isPresent() && (userRequest.getMaNguoiDung() == null || !existingEmail.get().getMaNguoiDung().equals(userRequest.getMaNguoiDung()))) {
            throw new DuplicateRecordException("Email '" + userRequest.getEmail() + "' đã được sử dụng.");
        }
        Optional<User> existingUsername = userRepository.findByTenDangNhapIgnoreCase(userRequest.getTenDangNhap());
        if (existingUsername.isPresent() && (userRequest.getMaNguoiDung() == null || !existingUsername.get().getMaNguoiDung().equals(userRequest.getMaNguoiDung()))) {
            throw new DuplicateRecordException("Tên đăng nhập '" + userRequest.getTenDangNhap() + "' đã tồn tại.");
        }

        User user;
        String oldAvatar = null;

        if (userRequest.getMaNguoiDung() != null) { // Sửa
            user = userRepository.findById(userRequest.getMaNguoiDung())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
            oldAvatar = user.getAnhDaiDien(); // Lấy tên ảnh cũ
        } else { // Thêm mới
            user = new User();
        }

        user.setHoTen(userRequest.getHoTen());
        user.setEmail(userRequest.getEmail());
        user.setTenDangNhap(userRequest.getTenDangNhap());
        user.setSoDienThoai(userRequest.getSoDienThoai());
        user.setTrangThai(userRequest.getTrangThai());

        // Xử lý ảnh đại diện
        if (StringUtils.hasText(userRequest.getAnhDaiDien())) {
            user.setAnhDaiDien(userRequest.getAnhDaiDien());
            // Nếu có ảnh cũ và nó khác ảnh mới, thì xóa file ảnh cũ đi
            if (StringUtils.hasText(oldAvatar) && !oldAvatar.equals(userRequest.getAnhDaiDien())) {
                storageService.deleteFile(oldAvatar);
            }
        }

        // Phần xử lý mật khẩu, vai trò, hạng thành viên giữ nguyên
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
        // Cân nhắc: Bạn có thể thêm logic xóa file ảnh đại diện ở đây
        userRepository.deleteById(id);
    }
}
