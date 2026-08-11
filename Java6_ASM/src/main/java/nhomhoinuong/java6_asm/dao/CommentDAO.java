package nhomhoinuong.java6_asm.dao;

import nhomhoinuong.java6_asm.bean.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentDAO extends JpaRepository<Comment, Long> {
    List<Comment> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    // Lọc theo số sao
    List<Comment> findByProductIdAndRatingOrderByCreatedAtDesc(Long productId, Integer rating);

    // Tính điểm trung bình
    @Query("SELECT COALESCE(AVG(c.rating), 5.0) FROM Comment c WHERE c.product.id = :productId")
    Double getAverageRating(Long productId);

    // Đếm số lượng đánh giá theo từng số sao
    Long countByProductIdAndRating(Long productId, Integer rating);
    
    Long countByProductId(Long productId);
}