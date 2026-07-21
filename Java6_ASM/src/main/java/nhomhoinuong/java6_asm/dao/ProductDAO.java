package nhomhoinuong.java6_asm.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import nhomhoinuong.java6_asm.bean.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.status = true " +
           // 🔥 ĐÃ THÊM: LOWER(p.category.name) LIKE ... để tìm theo tên danh mục
           "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) " +
           "ORDER BY " +
           "CASE WHEN :sortBy = 'price_asc' THEN p.price END ASC, " +
           "CASE WHEN :sortBy = 'price_desc' THEN p.price END DESC, " +
           "CASE WHEN :sortBy = 'newest' THEN p.createdAt END DESC, " +
           "CASE WHEN :sortBy IS NULL OR :sortBy = 'default' THEN p.id END DESC")
    List<Product> filterProducts(
        @Param("keyword") String keyword,
        @Param("categoryId") Long categoryId,
        @Param("brand") String brand,
        @Param("sortBy") String sortBy
    );
}