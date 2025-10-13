package nhom17.OneShop.service;

import nhom17.OneShop.entity.User;
import nhom17.OneShop.request.SignUpRequest;

public interface UserService {
    User registerNewUser(SignUpRequest signUpRequest);
}