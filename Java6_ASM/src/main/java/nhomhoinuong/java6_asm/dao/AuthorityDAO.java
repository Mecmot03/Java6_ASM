package nhomhoinuong.java6_asm.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nhomhoinuong.java6_asm.bean.Authority;

@Repository
public interface AuthorityDAO extends JpaRepository<Authority, Long> {

    // Lấy tất cả quyền của một User
    List<Authority> findByUserId(Long userId);

}