package nhomhoinuong.java6_asm.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nhomhoinuong.java6_asm.bean.User;

public interface UserDAO extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}