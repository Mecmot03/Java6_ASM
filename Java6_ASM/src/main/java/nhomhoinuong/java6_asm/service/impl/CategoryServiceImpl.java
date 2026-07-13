package nhomhoinuong.java6_asm.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import nhomhoinuong.java6_asm.bean.Category;
import nhomhoinuong.java6_asm.dao.CategoryDAO;
import nhomhoinuong.java6_asm.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDAO categoryDAO;

    @Override
    public List<Category> findAll() {
        return categoryDAO.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryDAO.findById(id).orElse(null);
    }

    @Override
    public Category create(Category category) {
        return categoryDAO.save(category);
    }

    @Override
    public Category update(Category category) {
        return categoryDAO.save(category);
    }

    @Override
    public void delete(Long id) {
        categoryDAO.deleteById(id);
    }
}