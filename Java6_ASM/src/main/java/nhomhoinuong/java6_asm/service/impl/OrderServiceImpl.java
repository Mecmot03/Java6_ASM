package nhomhoinuong.java6_asm.service.impl;

import nhomhoinuong.java6_asm.bean.CartItem;
import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.bean.OrderItem;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.CartItemDAO;
import nhomhoinuong.java6_asm.dao.OrderDAO;
import nhomhoinuong.java6_asm.dao.OrderItemDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.OrderRequest;
import nhomhoinuong.java6_asm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OrderItemDAO orderItemDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public Order createOrder(OrderRequest dto) {
        // 1. Kiểm tra User
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + dto.getUserId()));

        // 2. Lấy danh sách giỏ hàng từ DB
        List<CartItem> cartItems = cartItemDAO.findByUserId(dto.getUserId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn đang trống!");
        }

        // 3. Khởi tạo Đơn hàng
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setShippingAddress(dto.getShippingAddress());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderDAO.save(order);

        // 4. Lưu từng chi tiết sản phẩm & Tính tổng tiền chính xác từ DB
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal unitPrice = product.getPrice();
            orderItem.setPrice(unitPrice);

            BigDecimal itemSubTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemSubTotal);

            orderItemDAO.save(orderItem);
        }

        // Cập nhật tổng tiền hoàn chỉnh
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderDAO.save(savedOrder);

        // 5. Tự động dọn sạch giỏ hàng trong DB
        cartItemDAO.deleteByUserId(dto.getUserId());

        return savedOrder;
    }

    @Override
    public List<Order> getOrders(Long userId, String status) {
        if (userId == null) {
            if (status != null && !status.trim().isEmpty()) {
                return orderDAO.findAllByStatusOrderByOrderDateDesc(status);
            }
            return orderDAO.findAllByOrderByOrderDateDesc();
        }

        if (status != null && !status.trim().isEmpty()) {
            return orderDAO.findByUserIdAndStatusOrderByOrderDateDesc(userId, status);
        }
        return orderDAO.findByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    @Transactional
    public Order confirmOrder(Long orderId) {
        return transitionOrderStatus(orderId, "PENDING", "PROCESSING");
    }

    @Override
    @Transactional
    public Order shipOrder(Long orderId) {
        return transitionOrderStatus(orderId, "PROCESSING", "SHIPPING");
    }

    @Override
    @Transactional
    public Order deliverOrder(Long orderId) {
        return transitionOrderStatus(orderId, "SHIPPING", "DELIVERED");
    }

    @Override
    @Transactional
    public Order cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng.");
        }

        Order order = optionalOrder.get();
        String currentStatus = order.getStatus() == null ? "" : order.getStatus().trim().toUpperCase();
        if (!"PENDING".equals(currentStatus) && !"PROCESSING".equals(currentStatus)) {
            throw new RuntimeException("Chỉ có thể hủy đơn đang chờ xác nhận hoặc chờ lấy hàng.");
        }

        order.setStatus("CANCELLED");
        return orderDAO.save(order);
    }

    private Order transitionOrderStatus(Long orderId, String expectedCurrent, String nextStatus) {
        Optional<Order> optionalOrder = orderDAO.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đơn hàng.");
        }

        Order order = optionalOrder.get();
        String currentStatus = order.getStatus() == null ? "" : order.getStatus().trim().toUpperCase();

        if (!expectedCurrent.equals(currentStatus)) {
            throw new RuntimeException("Đơn hàng không ở trạng thái hợp lệ để cập nhật.");
        }

        order.setStatus(nextStatus);
        return orderDAO.save(order);
    }
}