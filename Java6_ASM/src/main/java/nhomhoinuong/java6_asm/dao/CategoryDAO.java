package nhomhoinuong.java6_asm.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import nhomhoinuong.java6_asm.bean.Category;

public interface CategoryDAO extends JpaRepository<Category, Long> {
    // Spring Data JPA sẽ tự động hiểu và sinh câu lệnh truy vấn theo status
    List<Category> findByStatus(Boolean status);
}