package nhomhoinuong.java6_asm.dao;

import java.util.List;

import nhomhoinuong.java6_asm.model.Product;

public interface ProductDAO {

    List<Product> findAll();

    Product findById(Long id);

}