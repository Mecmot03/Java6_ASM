package nhomhoinuong.java6_asm.service;

import java.util.List;

import nhomhoinuong.java6_asm.bean.Authority;

public interface AuthService {

    List<Authority> findAll();

    List<Authority> findByUserId(Long userId);

    Authority save(Authority authority);

    void delete(Long id);

}