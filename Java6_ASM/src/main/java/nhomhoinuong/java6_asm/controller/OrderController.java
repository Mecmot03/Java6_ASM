package nhomhoinuong.java6_asm.controller;

import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.dto.OrderRequest;
import nhomhoinuong.java6_asm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // API ĐẶT HÀNG
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest dto) {
        try {
            return ResponseEntity.ok(orderService.createOrder(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi tạo đơn hàng: " + e.getMessage());
        }
    }

    // API LẤY DANH SÁCH ĐƠN HÀNG
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByUser(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getOrders(userId, status));
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId) {
        try {
            orderService.confirmOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã chuyển sang chờ lấy hàng.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable Long orderId) {
        try {
            orderService.shipOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã chuyển sang chờ giao hàng.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/deliver")
    public ResponseEntity<?> deliverOrder(@PathVariable Long orderId) {
        try {
            orderService.deliverOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã được xác nhận giao thành công.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã được hủy.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}