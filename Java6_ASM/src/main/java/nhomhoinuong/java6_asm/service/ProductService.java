package nhomhoinuong.java6_asm.service;

import java.util.List;
import nhomhoinuong.java6_asm.bean.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);

    List<Product> filterProducts(String keyword, Long categoryId, String brand, String sortBy);

    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
}