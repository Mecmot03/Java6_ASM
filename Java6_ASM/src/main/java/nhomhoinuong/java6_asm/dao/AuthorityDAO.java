package nhomhoinuong.java6_asm.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import nhomhoinuong.java6_asm.bean.Authority;

public interface AuthorityDAO extends JpaRepository<Authority, Long> {

    // 🔴 SỬA TỪ Optional SANG List
    List<Authority> findByUser_Id(Long userId);

}