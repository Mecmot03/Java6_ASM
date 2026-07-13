package nhomhoinuong.java6_asm.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nhomhoinuong.java6_asm.bean.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {

}

//đoạn này qnhi test thôi, cứ để đó nhé
//package nhomhoinuong.java6_asm.dao;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import nhomhoinuong.java6_asm.bean.Product;
//
//@Repository
//public interface ProductDAO extends JpaRepository<Product, Long> {
//    // Spring Data JPA sẽ tự động tạo các hàm CRUD cơ bản cho Product dựa vào đây
//}