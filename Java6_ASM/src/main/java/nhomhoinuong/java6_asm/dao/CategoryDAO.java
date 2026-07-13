package nhomhoinuong.java6_asm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import nhomhoinuong.java6_asm.bean.Category;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Long> {

}