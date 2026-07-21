package nhomhoinuong.java6_asm.service;

import java.util.List;
import nhomhoinuong.java6_asm.bean.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    
    // Nâng cấp thêm tham số keyword
    List<Product> filterProducts(String keyword, Long categoryId, String brand, String sortBy);
}