//package nhom17.OneShop.service.impl;
//
//import nhom17.OneShop.entity.MembershipTier;
//import nhom17.OneShop.entity.Role;
//import nhom17.OneShop.entity.User;
//import nhom17.OneShop.exception.DuplicateRecordException;
//import nhom17.OneShop.exception.NotFoundException;
//import nhom17.OneShop.repository.MembershipTierRepository;
//import nhom17.OneShop.repository.OrderRepository;
//import nhom17.OneShop.repository.RoleRepository;
//import nhom17.OneShop.entity.Role;
//import nhom17.OneShop.repository.UserRepository;
//import nhom17.OneShop.request.UserRequest;
//import nhom17.OneShop.service.StorageService;
//import nhom17.OneShop.request.SignUpRequest;
//import nhom17.OneShop.service.UserService;
//import nhom17.OneShop.specification.UserSpecification;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.StringUtils;
//
//import java.util.Optional;
//
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private MembershipTierRepository membershipTierRepository;
//    @Autowired
//    private StorageService storageService;
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Override
//    public Page<User> findAll(String keyword, Integer roleId, Integer tierId, Integer status, int page, int size) {
//        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("maNguoiDung").descending());
//
//        Specification<User> spec = (root, query, cb) -> cb.conjunction();
//        if (StringUtils.hasText(keyword)) {
//            spec = spec.and(UserSpecification.hasUsername(keyword));
//        }
//        if (roleId != null) {
//            spec = spec.and(UserSpecification.hasRole(roleId));
//        }
//        if (tierId != null) {
//            spec = spec.and(UserSpecification.hasMembershipTier(tierId));
//        }
//        if (status != null) {
//            spec = spec.and(UserSpecification.hasStatus(status));
//        }
//
//        return userRepository.findAll(spec, pageable);
//    }
//
//    @Override
//    public User findById(int id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với ID: " + id));
//    }
//
//    @Override
//    @Transactional
//    public void save(UserRequest userRequest) {
//        validateUniqueFields(userRequest);
//        User user = prepareUserEntity(userRequest);
//        String oldAvatar = user.getAnhDaiDien(); // Lấy ảnh cũ trước khi map ảnh mới
//        mapRequestToEntity(userRequest, user);
//        userRepository.save(user);
//
//        // Xóa ảnh cũ nếu có ảnh mới được upload
//        if (StringUtils.hasText(userRequest.getAnhDaiDien()) && StringUtils.hasText(oldAvatar) && !oldAvatar.equals(userRequest.getAnhDaiDien())) {
//            storageService.deleteFile(oldAvatar);
//        }
//    }
//
//    private void validateUniqueFields(UserRequest request) {
//        if (request.getMaNguoiDung() == null) { // Tạo mới
//            if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
//                throw new DuplicateRecordException("Email '" + request.getEmail() + "' đã được sử dụng.");
//            }
//            if (userRepository.existsByTenDangNhapIgnoreCase(request.getTenDangNhap())) {
//                throw new DuplicateRecordException("Tên đăng nhập '" + request.getTenDangNhap() + "' đã tồn tại.");
//            }
//        } else { // Cập nhật
//            if (userRepository.existsByEmailIgnoreCaseAndMaNguoiDungNot(request.getEmail(), request.getMaNguoiDung())) {
//                throw new DuplicateRecordException("Email '" + request.getEmail() + "' đã được người dùng khác sử dụng.");
//            }
//            if (userRepository.existsByTenDangNhapIgnoreCaseAndMaNguoiDungNot(request.getTenDangNhap(), request.getMaNguoiDung())) {
//                throw new DuplicateRecordException("Tên đăng nhập '" + request.getTenDangNhap() + "' đã được người dùng khác sử dụng.");
//            }
//        }
//    }
//
//    private User prepareUserEntity(UserRequest userRequest) {
//        if (userRequest.getMaNguoiDung() != null) {
//            return findById(userRequest.getMaNguoiDung());
//        }
//        return new User();
//    }
//
//    private void mapRequestToEntity(UserRequest request, User user) {
//        user.setHoTen(request.getHoTen());
//        user.setEmail(request.getEmail());
//        user.setTenDangNhap(request.getTenDangNhap());
//        user.setSoDienThoai(request.getSoDienThoai());
//        user.setTrangThai(request.getTrangThai());
//        if (StringUtils.hasText(request.getAnhDaiDien())) {
//            user.setAnhDaiDien(request.getAnhDaiDien());
//        }
//        if (StringUtils.hasText(request.getMatKhau())) {
//            user.setMatKhau(request.getMatKhau());
//        }
//        Role role = roleRepository.findById(request.getMaVaiTro())
//                .orElseThrow(() -> new NotFoundException("Vai trò không hợp lệ với ID: " + request.getMaVaiTro()));
//        user.setVaiTro(role);
//        if (request.getMaHangThanhVien() != null) {
//            MembershipTier tier = membershipTierRepository.findById(request.getMaHangThanhVien())
//                    .orElseThrow(() -> new NotFoundException("Hạng thành viên không hợp lệ với ID: " + request.getMaHangThanhVien()));
//            user.setHangThanhVien(tier);
//        } else {
//            user.setHangThanhVien(null);
//        }
//    }
//
//    /**
//     * ✅ THAY ĐỔI BÊN TRONG:
//     * - Bổ sung logic kiểm tra ràng buộc và xóa ảnh.
//     */
//    @Override
//    @Transactional(noRollbackFor = DataIntegrityViolationException.class)
//    public void delete(int id) {
//        User userToDelete = findById(id);
//
//        if (orderRepository.existsByNguoiDung_MaNguoiDung(id)) {
//            userToDelete.setTrangThai(0);
//            userRepository.save(userToDelete);
//            throw new DataIntegrityViolationException("Không thể xóa người dùng '" + userToDelete.getHoTen() + "' vì đã có lịch sử đặt hàng. Tài khoản đã được chuyển sang trạng thái 'Khóa'.");
//        }
//
//        if (StringUtils.hasText(userToDelete.getAnhDaiDien())) {
//            storageService.deleteFile(userToDelete.getAnhDaiDien());
//        }
//        userRepository.delete(userToDelete);
//    }
//
//
////    User
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public User registerNewUser(SignUpRequest signUpRequest) {
//        // Kiểm tra xem email đã tồn tại chưa
//        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
//            throw new RuntimeException("Email đã tồn tại: " + signUpRequest.getEmail());
//        }
//
//        if (userRepository.findByTenDangNhap(signUpRequest.getTenDangNhap()).isPresent()) {
//            throw new RuntimeException("Tên đăng nhập đã tồn tại: " + signUpRequest.getTenDangNhap());
//        }
//
//        User newUser = new User();
//        newUser.setHoTen(signUpRequest.getHoTen());
//        newUser.setEmail(signUpRequest.getEmail());
//        newUser.setTenDangNhap(signUpRequest.getTenDangNhap());
//        // ✅ MÃ HÓA MẬT KHẨU TRƯỚC KHI LƯU
//        newUser.setMatKhau(passwordEncoder.encode(signUpRequest.getPassword()));
//
//        // Thiết lập vai trò mặc định cho người dùng mới là "USER"
//        Role userRole = new Role();
//        userRole.setMaVaiTro(2); // Giả sử ID của vai trò "User" là 2
//        newUser.setVaiTro(userRole);
//        newUser.setTrangThai(1); // Kích hoạt tài khoản
//
//        return userRepository.save(newUser);
//    }
//}
package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.TemporaryRegister;
import nhom17.OneShop.entity.MembershipTier;
import nhom17.OneShop.entity.Role;
import nhom17.OneShop.entity.User;
import nhom17.OneShop.exception.DuplicateRecordException;
import nhom17.OneShop.exception.NotFoundException;
import nhom17.OneShop.repository.*;
import nhom17.OneShop.repository.TemporaryRegositerRepository;
import nhom17.OneShop.request.SignUpRequest;
import nhom17.OneShop.request.UserRequest;
import nhom17.OneShop.service.OtpService; // Bổ sung
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime; // Bổ sung
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MembershipTierRepository membershipTierRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== BỔ SUNG CÁC DEPENDENCY CẦN THIẾT ====================
    @Autowired
    private TemporaryRegositerRepository dangKyTamThoiRepository;

    @Autowired
    private OtpService otpService;

    // (Các hàm findAll, findById, save, delete... giữ nguyên)
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

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng với email: " + email));
    }

    // ==================== THAY THẾ/BỔ SUNG CÁC HÀM XỬ LÝ ĐĂNG KÝ, OTP, QUÊN MẬT KHẨU ====================

    @Override
    @Transactional
    public User registerNewUser(SignUpRequest signUpRequest) {
        // Kiểm tra email đã được đăng ký chính thức chưa
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được đăng ký: " + signUpRequest.getEmail());
        }

        // Kiểm tra tên đăng nhập
        if (userRepository.findByTenDangNhap(signUpRequest.getTenDangNhap()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại: " + signUpRequest.getTenDangNhap());
        }

        // Xóa đăng ký tạm thời cũ của email này nếu có
        dangKyTamThoiRepository.deleteByEmail(signUpRequest.getEmail());

        // Lưu thông tin đăng ký tạm thời
        TemporaryRegister dangKyTam = new TemporaryRegister();
        dangKyTam.setEmail(signUpRequest.getEmail());
        dangKyTam.setTenDangNhap(signUpRequest.getTenDangNhap());
        dangKyTam.setMatKhau(passwordEncoder.encode(signUpRequest.getPassword()));
        dangKyTam.setHoTen(signUpRequest.getHoTen());
        dangKyTam.setHetHanLuc(LocalDateTime.now().plusMinutes(30)); // Hết hạn sau 30 phút

        dangKyTamThoiRepository.save(dangKyTam);

        // Tạo và gửi OTP
        otpService.generateOtpForEmail(signUpRequest.getEmail(), "Đăng ký");

        // Trả về null vì người dùng chưa được tạo chính thức
        return null;
    }

    @Override
    @Transactional
    public boolean verifyEmailOtp(String email, String otp) {
        // Xác thực OTP
        boolean isValid = otpService.validateOtp(email, otp, "Đăng ký");

        if (!isValid) {
            return false;
        }

        // Lấy thông tin đăng ký tạm
        TemporaryRegister dangKyTam = dangKyTamThoiRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký cho email này."));

        // Kiểm tra hết hạn
        if (dangKyTam.getHetHanLuc().isBefore(LocalDateTime.now())) {
            dangKyTamThoiRepository.delete(dangKyTam);
            throw new RuntimeException("Phiên đăng ký đã hết hạn. Vui lòng đăng ký lại.");
        }

        // Tạo tài khoản chính thức
        User newUser = new User();
        newUser.setHoTen(dangKyTam.getHoTen());
        newUser.setEmail(dangKyTam.getEmail());
        newUser.setTenDangNhap(dangKyTam.getTenDangNhap());
        newUser.setMatKhau(dangKyTam.getMatKhau()); // Mật khẩu đã được mã hóa

        Role userRole = new Role();
        userRole.setMaVaiTro(2); // Giả sử ID của vai trò "User" là 2
        newUser.setVaiTro(userRole);

        newUser.setTrangThai(1); // Kích hoạt
        newUser.setXacThucEmail(true); // Đánh dấu đã xác thực email

        userRepository.save(newUser);

        // Xóa thông tin đăng ký tạm
        dangKyTamThoiRepository.delete(dangKyTam);

        return true;
    }

    @Override
    @Transactional
    public void sendResetPasswordOtp(String email) {
        // Kiểm tra email có tồn tại không
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống!"));

        // Tạo và gửi OTP
        otpService.generateOtpForEmail(email, "Quên mật khẩu");
    }

    @Override
    @Transactional
    public boolean verifyResetPasswordOtp(String email, String otp) {
        return otpService.validateOtp(email, otp, "Quên mật khẩu");
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Mã hóa và cập nhật mật khẩu mới
        user.setMatKhau(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}