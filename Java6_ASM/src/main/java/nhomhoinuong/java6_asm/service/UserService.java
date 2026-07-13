package nhomhoinuong.java6_asm.service;

import java.util.List;
import java.util.Optional;

import nhomhoinuong.java6_asm.bean.User;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    void delete(Long id);

    boolean existsByEmail(String email);

}