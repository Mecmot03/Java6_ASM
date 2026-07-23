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
import java.util.Optional;

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
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        
        if (userId == null) {
            if (status != null && !status.isEmpty()) {
                return ResponseEntity.ok(orderDAO.findAllByStatusOrderByOrderDateDesc(status));
            }
            return ResponseEntity.ok(orderDAO.findAllByOrderByOrderDateDesc());
        }

        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(orderDAO.findByUserIdAndStatusOrderByOrderDateDesc(userId, status));
        }
        return ResponseEntity.ok(orderDAO.findByUserIdOrderByOrderDateDesc(userId));
    }

    @PutMapping("/{orderId}/confirm")
    @Transactional
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId) {
        return transitionOrderStatus(orderId, "PENDING", "PROCESSING", "Đơn hàng đã chuyển sang chờ lấy hàng.");
    }

    @PutMapping("/{orderId}/ship")
    @Transactional
    public ResponseEntity<?> shipOrder(@PathVariable Long orderId) {
        return transitionOrderStatus(orderId, "PROCESSING", "SHIPPING", "Đơn hàng đã chuyển sang chờ giao hàng.");
    }

    @PutMapping("/{orderId}/deliver")
    @Transactional
    public ResponseEntity<?> deliverOrder(@PathVariable Long orderId) {
        return transitionOrderStatus(orderId, "SHIPPING", "DELIVERED", "Đơn hàng đã được xác nhận giao thành công.");
    }

    @PutMapping("/{orderId}/cancel")
    @Transactional
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        return cancelOrderByCurrentStatus(orderId);
    }

    private ResponseEntity<?> cancelOrderByCurrentStatus(Long orderId) {
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }

        Order order = optionalOrder.get();
        String currentStatus = order.getStatus() == null ? "" : order.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(currentStatus) && !"PROCESSING".equals(currentStatus)) {
            return ResponseEntity.badRequest().body("Chỉ có thể hủy đơn đang chờ xác nhận hoặc chờ lấy hàng.");
        }

        order.setStatus("CANCELLED");
        orderDAO.save(order);
        return ResponseEntity.ok("Đơn hàng đã được hủy.");
    }

    private ResponseEntity<?> transitionOrderStatus(Long orderId, String expectedCurrent, String nextStatus, String successMessage) {
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }

        Order order = optionalOrder.get();
        String currentStatus = order.getStatus() == null ? "" : order.getStatus().trim().toUpperCase();

        if (!expectedCurrent.equals(currentStatus)) {
            return ResponseEntity.badRequest().body("Đơn hàng không ở trạng thái hợp lệ để cập nhật.");
        }

        order.setStatus(nextStatus);
        orderDAO.save(order);
        return ResponseEntity.ok(successMessage);
    }
}