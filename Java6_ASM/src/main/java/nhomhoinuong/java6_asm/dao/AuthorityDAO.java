package nhomhoinuong.java6_asm.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nhomhoinuong.java6_asm.bean.Authority;

public interface AuthorityDAO extends JpaRepository<Authority, Long> {

    Optional<Authority> findByUser_Id(Long userId);

}