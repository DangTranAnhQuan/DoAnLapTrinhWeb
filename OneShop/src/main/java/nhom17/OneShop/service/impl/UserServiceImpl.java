package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.User;
import nhom17.OneShop.entity.Role;
import nhom17.OneShop.repository.UserRepository;
import nhom17.OneShop.request.SignUpRequest;
import nhom17.OneShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerNewUser(SignUpRequest signUpRequest) {
        // Kiểm tra xem email đã tồn tại chưa
        if (nguoiDungRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại: " + signUpRequest.getEmail());
        }

        if (nguoiDungRepository.findByTenDangNhap(signUpRequest.getTenDangNhap()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại: " + signUpRequest.getTenDangNhap());
        }

        User newUser = new User();
        newUser.setHoTen(signUpRequest.getHoTen());
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setTenDangNhap(signUpRequest.getTenDangNhap());
        // ✅ MÃ HÓA MẬT KHẨU TRƯỚC KHI LƯU
        newUser.setMatKhau(passwordEncoder.encode(signUpRequest.getPassword()));

        // Thiết lập vai trò mặc định cho người dùng mới là "USER"
        Role userRole = new Role();
        userRole.setMaVaiTro(2); // Giả sử ID của vai trò "User" là 2
        newUser.setVaiTro(userRole);
        newUser.setTrangThai(1); // Kích hoạt tài khoản

        return nguoiDungRepository.save(newUser);
    }
}