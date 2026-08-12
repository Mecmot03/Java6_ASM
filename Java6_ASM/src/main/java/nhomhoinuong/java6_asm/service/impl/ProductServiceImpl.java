package nhomhoinuong.java6_asm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhomhoinuong.java6_asm.bean.Category;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.dao.CartItemDAO;
import nhomhoinuong.java6_asm.dao.CategoryDAO;
import nhomhoinuong.java6_asm.dao.CommentDAO;
import nhomhoinuong.java6_asm.dao.FavoriteDAO;
import nhomhoinuong.java6_asm.dao.OrderItemDAO;
import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private FavoriteDAO favoriteDAO;

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private OrderItemDAO orderItemDAO;

    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productDAO.findById(id).orElse(null);
    }

    @Override
    public List<Product> filterProducts(String keyword, Long categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, String sortBy) {
        String cleanKeyword = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            cleanKeyword = keyword.replace("+", " ").trim();
            if (cleanKeyword.isEmpty()) {
                cleanKeyword = null;
            }
        }

        String cleanBrand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : null;
        String cleanSort = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy.trim() : null;

        return productDAO.filterProducts(cleanKeyword, categoryId, cleanBrand, minPrice, maxPrice, cleanSort);
    }

    @Override
    public Product createProduct(Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryDAO.findById(product.getCategory().getId()).orElse(null);
            product.setCategory(category);
        }

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productDAO.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product oldProduct = productDAO.findById(id).orElse(null);
        if (oldProduct == null) {
            return null;
        }

        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryDAO.findById(product.getCategory().getId()).orElse(null);
            oldProduct.setCategory(category);
        } else {
            oldProduct.setCategory(product.getCategory());
        }

        oldProduct.setDiscountId(product.getDiscountId());
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setQuantity(product.getQuantity());
        oldProduct.setImage(product.getImage());
        oldProduct.setBrand(product.getBrand());
        oldProduct.setStatus(product.getStatus());
        oldProduct.setUpdatedAt(LocalDateTime.now());

        return productDAO.save(oldProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productDAO.existsById(id)) {
            return;
        }

        // 1. Dọn sạch chi tiết đơn hàng liên quan đến sản phẩm này
        orderItemDAO.findAll().stream()
                .filter(oi -> id.equals(oi.getProductId()))
                .forEach(orderItemDAO::delete);

        // 2. Dọn sạch giỏ hàng, danh sách yêu thích và bình luận
        cartItemDAO.findAll().stream()
                .filter(ci -> ci.getProduct() != null && id.equals(ci.getProduct().getId()))
                .forEach(cartItemDAO::delete);

        favoriteDAO.findAll().stream()
                .filter(f -> f.getProduct() != null && id.equals(f.getProduct().getId()))
                .forEach(favoriteDAO::delete);

        commentDAO.findAll().stream()
                .filter(c -> c.getProduct() != null && id.equals(c.getProduct().getId()))
                .forEach(commentDAO::delete);

        // 3. Xóa vĩnh viễn sản phẩm ra khỏi CSDL SQL Server
        productDAO.deleteById(id);
    }
}