package nhomhoinuong.java6_asm.dao;

import nhomhoinuong.java6_asm.bean.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderDAO extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    List<Order> findByUserIdAndStatusOrderByOrderDateDesc(Long userId, String status);
    List<Order> findAllByOrderByOrderDateDesc();
    List<Order> findAllByStatusOrderByOrderDateDesc(String status);
}