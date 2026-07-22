package nhomhoinuong.java6_asm.controller;

import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.bean.OrderItem;
import nhomhoinuong.java6_asm.dao.CartItemDAO;
import nhomhoinuong.java6_asm.dao.OrderItemDAO;
import nhomhoinuong.java6_asm.dao.OrderDAO;
import nhomhoinuong.java6_asm.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OrderItemDAO orderItemDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    // API ĐẶT HÀNG
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest dto) {
        try {
            // 1. Lưu đơn hàng
            Order order = new Order();
            order.setUserId(dto.getUserId());
            order.setReceiverName(dto.getReceiverName());
            order.setReceiverPhone(dto.getReceiverPhone());
            order.setShippingAddress(dto.getShippingAddress());
            order.setPaymentMethod(dto.getPaymentMethod());
            order.setTotalAmount(dto.getTotalAmount());
            order.setStatus("PENDING");

            Order savedOrder = orderDAO.save(order);

            // 2. Lưu chi tiết sản phẩm trong đơn hàng
            for (OrderRequest.OrderItemDTO itemDto : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrderId(savedOrder.getId());
                item.setProductId(itemDto.getProductId());
                item.setQuantity(itemDto.getQuantity());
                item.setPrice(itemDto.getPrice());
                orderItemDAO.save(item);
            }

            // 3. Xóa sạch giỏ hàng của user sau khi đặt thành công
            cartItemDAO.deleteByUserId(dto.getUserId());

            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    // API LẤY DANH SÁCH ĐƠN HÀNG
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByUser(
            @RequestParam Long userId,
            @RequestParam(required = false) String status) {
        
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(orderDAO.findByUserIdAndStatusOrderByOrderDateDesc(userId, status));
        }
        return ResponseEntity.ok(orderDAO.findByUserIdOrderByOrderDateDesc(userId));
    }
}