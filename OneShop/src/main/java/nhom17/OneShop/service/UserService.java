package nhom17.OneShop.service;

import nhom17.OneShop.entity.NguoiDung;
import nhom17.OneShop.request.SignUpRequest;

public interface UserService {
    NguoiDung registerNewUser(SignUpRequest signUpRequest);
}