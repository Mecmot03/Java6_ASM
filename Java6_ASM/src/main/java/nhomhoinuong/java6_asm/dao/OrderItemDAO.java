package nhomhoinuong.java6_asm.dao;

import nhomhoinuong.java6_asm.bean.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDAO extends JpaRepository<OrderItem, Long> {
}