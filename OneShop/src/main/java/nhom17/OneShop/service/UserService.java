package nhom17.OneShop.service;

import nhom17.OneShop.entity.User;
import nhom17.OneShop.request.UserRequest;
import org.springframework.data.domain.Page;
import nhom17.OneShop.request.SignUpRequest;

public interface UserService {
    Page<User> findAll(String keyword, Integer roleId, Integer tierId, Integer status, int page, int size);
    User findById(int id);
    void save(UserRequest userRequest);
    void delete(int id);

    User registerNewUser(SignUpRequest signUpRequest);
}