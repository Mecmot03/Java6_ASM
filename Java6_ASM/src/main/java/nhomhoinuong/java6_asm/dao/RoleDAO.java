package nhomhoinuong.java6_asm.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import nhomhoinuong.java6_asm.bean.Role;

public interface RoleDAO extends JpaRepository<Role, Long> {

    // Tìm Role theo tên (VD: "ROLE_USER", "ROLE_ADMIN")
    Optional<Role> findByName(String name);
    
}