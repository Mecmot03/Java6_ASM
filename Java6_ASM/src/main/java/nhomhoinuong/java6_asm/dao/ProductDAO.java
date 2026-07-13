package nhomhoinuong.java6_asm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nhomhoinuong.java6_asm.model.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {

}