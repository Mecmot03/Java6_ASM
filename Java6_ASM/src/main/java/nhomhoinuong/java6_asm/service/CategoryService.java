package nhomhoinuong.java6_asm.service;

import java.util.List;
import nhomhoinuong.java6_asm.bean.Category;

public interface CategoryService {
    List<Category> findAll();
    
    Category findById(Long id);
    
    Category create(Category category);
    
    Category update(Category category);
    
    void delete(Long id);
}