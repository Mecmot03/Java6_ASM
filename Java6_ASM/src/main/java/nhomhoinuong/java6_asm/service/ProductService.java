package nhomhoinuong.java6_asm.service;

import java.util.List;

import nhomhoinuong.java6_asm.model.Product;

public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(Long id);

}