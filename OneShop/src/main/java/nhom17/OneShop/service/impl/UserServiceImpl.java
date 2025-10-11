package nhom17.OneShop.service.impl;

import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.entity.VaiTro;
import nhom17.OneShop.repository.NguoiDungRepository;
import nhom17.OneShop.request.SignUpRequest;
import nhom17.OneShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public NguoiDung registerNewUser(SignUpRequest signUpRequest) {
        // Kiểm tra xem email đã tồn tại chưa
        if (nguoiDungRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại: " + signUpRequest.getEmail());
        }

        if (nguoiDungRepository.findByTenDangNhap(signUpRequest.getTenDangNhap()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại: " + signUpRequest.getTenDangNhap());
        }

        NguoiDung newUser = new NguoiDung();
        newUser.setHoTen(signUpRequest.getHoTen());
        newUser.setEmail(signUpRequest.getEmail());
        newUser.setTenDangNhap(signUpRequest.getTenDangNhap());
        // ✅ MÃ HÓA MẬT KHẨU TRƯỚC KHI LƯU
        newUser.setMatKhau(passwordEncoder.encode(signUpRequest.getPassword()));

        // Thiết lập vai trò mặc định cho người dùng mới là "USER"
        VaiTro userRole = new VaiTro();
        userRole.setMaVaiTro(2); // Giả sử ID của vai trò "User" là 2
        newUser.setVaiTro(userRole);
        newUser.setTrangThai(1); // Kích hoạt tài khoản

        return nguoiDungRepository.save(newUser);
    }
}