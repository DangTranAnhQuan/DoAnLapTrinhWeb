package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.entity.Role;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.exception.NotFoundException;
import nhom17.OneShop.repository.MembershipTierRepository;
import nhom17.OneShop.repository.OrderRepository;
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
    @Autowired
    private OrderRepository orderRepository;

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
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + id));
    }

    @Override
    @Transactional
    public void save(UserRequest userRequest) {
        validateUniqueFields(userRequest);
        User user = prepareUserEntity(userRequest);
        String oldAvatar = user.getAnhDaiDien(); // Lấy ảnh cũ trước khi map ảnh mới
        mapRequestToEntity(userRequest, user);
        userRepository.save(user);

        // Xóa ảnh cũ nếu có ảnh mới được upload
        if (StringUtils.hasText(userRequest.getAnhDaiDien()) && StringUtils.hasText(oldAvatar) && !oldAvatar.equals(userRequest.getAnhDaiDien())) {
            storageService.deleteFile(oldAvatar);
        }
    }

    private void validateUniqueFields(UserRequest request) {
        if (request.getMaNguoiDung() == null) { // Tạo mới
            if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
                throw new DuplicateRecordException("Email '" + request.getEmail() + "' đã được sử dụng.");
            }
            if (userRepository.existsByTenDangNhapIgnoreCase(request.getTenDangNhap())) {
                throw new DuplicateRecordException("Tên đăng nhập '" + request.getTenDangNhap() + "' đã tồn tại.");
            }
        } else { // Cập nhật
            if (userRepository.existsByEmailIgnoreCaseAndMaNguoiDungNot(request.getEmail(), request.getMaNguoiDung())) {
                throw new DuplicateRecordException("Email '" + request.getEmail() + "' đã được người dùng khác sử dụng.");
            }
            if (userRepository.existsByTenDangNhapIgnoreCaseAndMaNguoiDungNot(request.getTenDangNhap(), request.getMaNguoiDung())) {
                throw new DuplicateRecordException("Tên đăng nhập '" + request.getTenDangNhap() + "' đã được người dùng khác sử dụng.");
            }
        }
    }

    private User prepareUserEntity(UserRequest userRequest) {
        if (userRequest.getMaNguoiDung() != null) {
            return findById(userRequest.getMaNguoiDung());
        }
        return new User();
    }

    private void mapRequestToEntity(UserRequest request, User user) {
        user.setHoTen(request.getHoTen());
        user.setEmail(request.getEmail());
        user.setTenDangNhap(request.getTenDangNhap());
        user.setSoDienThoai(request.getSoDienThoai());
        user.setTrangThai(request.getTrangThai());
        if (StringUtils.hasText(request.getAnhDaiDien())) {
            user.setAnhDaiDien(request.getAnhDaiDien());
        }
        if (StringUtils.hasText(request.getMatKhau())) {
            user.setMatKhau(request.getMatKhau());
        }
        Role role = roleRepository.findById(request.getMaVaiTro())
                .orElseThrow(() -> new NotFoundException("Vai trò không hợp lệ với ID: " + request.getMaVaiTro()));
        user.setVaiTro(role);
        if (request.getMaHangThanhVien() != null) {
            MembershipTier tier = membershipTierRepository.findById(request.getMaHangThanhVien())
                    .orElseThrow(() -> new NotFoundException("Hạng thành viên không hợp lệ với ID: " + request.getMaHangThanhVien()));
            user.setHangThanhVien(tier);
        } else {
            user.setHangThanhVien(null);
        }
    }

    /**
     * ✅ THAY ĐỔI BÊN TRONG:
     * - Bổ sung logic kiểm tra ràng buộc và xóa ảnh.
     */
    @Override
    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
    public void delete(int id) {
        User userToDelete = findById(id);

        if (orderRepository.existsByNguoiDung_MaNguoiDung(id)) {
            userToDelete.setTrangThai(0);
            userRepository.save(userToDelete);
            throw new DataIntegrityViolationException("Không thể xóa người dùng '" + userToDelete.getHoTen() + "' vì đã có lịch sử đặt hàng. Tài khoản đã được chuyển sang trạng thái 'Khóa'.");
        }

        if (StringUtils.hasText(userToDelete.getAnhDaiDien())) {
            storageService.deleteFile(userToDelete.getAnhDaiDien());
        }
        userRepository.delete(userToDelete);
    }
}
