package nhomhoinuong.java6_asm.service;

import java.util.List;
import java.util.Optional;

import nhomhoinuong.java6_asm.bean.Authority;

public interface AuthService {

    List<Authority> findAll();

    Optional<Authority> findByUserId(Long userId);

    Authority save(Authority authority);

    void delete(Long id);

}