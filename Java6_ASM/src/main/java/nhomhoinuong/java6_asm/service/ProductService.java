package nhomhoinuong.java6_asm.service;

import java.math.BigDecimal;
import java.util.List;
import nhomhoinuong.java6_asm.bean.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);

    // Đã thêm tham số Boolean status ở cuối
    List<Product> filterProducts(String keyword, Long categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, String sortBy, Boolean status);

    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}