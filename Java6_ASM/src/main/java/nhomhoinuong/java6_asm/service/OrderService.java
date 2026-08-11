package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.dto.OrderRequest;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderRequest dto);
    List<Order> getOrders(Long userId, String status);
    Order confirmOrder(Long orderId);
    Order shipOrder(Long orderId);
    Order deliverOrder(Long orderId);
    Order cancelOrder(Long orderId);
    void rebuyOrder(Long orderId, Long userId); // Thêm hàm mua lại đơn hàng
}