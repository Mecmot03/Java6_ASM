package nhomhoinuong.java6_asm.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Category;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.dao.*;
import nhomhoinuong.java6_asm.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;
    private final ProductDAO productDAO;
    private final CartItemDAO cartItemDAO;
    private final FavoriteDAO favoriteDAO;
    private final CommentDAO commentDAO;
    private final OrderItemDAO orderItemDAO;

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

    // 🔴 XÓA CỨNG HOÀN TOÀN DANH MỤC VÀ TẤT CẢ SẢN PHẨM CON
    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryDAO.findById(id).orElse(null);
        if (category == null) {
            return;
        }

        // 1. Tìm tất cả sản phẩm thuộc danh mục này
        List<Product> products = productDAO.findAll().stream()
                .filter(p -> p.getCategory() != null && id.equals(p.getCategory().getId()))
                .toList();

        // 2. Dọn sạch toàn bộ ràng buộc của từng sản phẩm trong các bảng phụ
        for (Product product : products) {
            Long productId = product.getId();

            cartItemDAO.findAll().stream()
                    .filter(ci -> ci.getProduct() != null && productId.equals(ci.getProduct().getId()))
                    .forEach(cartItemDAO::delete);

            favoriteDAO.findAll().stream()
                    .filter(f -> f.getProduct() != null && productId.equals(f.getProduct().getId()))
                    .forEach(favoriteDAO::delete);

            commentDAO.findAll().stream()
                    .filter(c -> c.getProduct() != null && productId.equals(c.getProduct().getId()))
                    .forEach(commentDAO::delete);

            orderItemDAO.findAll().stream()
                    .filter(oi -> productId.equals(oi.getProductId()))
                    .forEach(orderItemDAO::delete);

            productDAO.delete(product);
        }

        // 3. Xóa vĩnh viễn danh mục khỏi CSDL (đã đưa ra ngoài vòng lặp for)
        categoryDAO.delete(category);
    }
      
    @Override
    public List<Category> findByStatus(Boolean status) {
        // 🛠️ FIX: Tránh lỗi truyền tham số null vào câu lệnh truy vấn của JPA
        if (status == null) {
            return categoryDAO.findAll();
        }
        return categoryDAO.findByStatus(status);
    }
}