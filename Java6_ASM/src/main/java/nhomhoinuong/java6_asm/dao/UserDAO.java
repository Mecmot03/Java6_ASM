package nhomhoinuong.java6_asm.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nhomhoinuong.java6_asm.bean.User;

@Repository
public interface UserDAO extends JpaRepository<User, Long> {

    // Đăng nhập
    Optional<User> findByEmail(String email);

    // Kiểm tra email đã tồn tại
    boolean existsByEmail(String email);

}