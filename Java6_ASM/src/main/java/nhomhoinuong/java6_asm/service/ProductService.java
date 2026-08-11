package nhomhoinuong.java6_asm.service;

import java.math.BigDecimal;
import java.util.List;
import nhomhoinuong.java6_asm.bean.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);

    // Bổ sung minPrice và maxPrice
    List<Product> filterProducts(String keyword, Long categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, String sortBy);

    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}