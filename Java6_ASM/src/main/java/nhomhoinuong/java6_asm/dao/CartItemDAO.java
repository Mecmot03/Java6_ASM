package nhomhoinuong.java6_asm.dao;

import nhomhoinuong.java6_asm.bean.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemDAO extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user.id = :userId")
    Integer countTotalItemsByUserId(@Param("userId") Long userId);
}