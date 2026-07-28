package nhomhoinuong.java6_asm.service.impl;

import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.bean.OrderItem;
import nhomhoinuong.java6_asm.dao.CartItemDAO;
import nhomhoinuong.java6_asm.dao.OrderDAO;
import nhomhoinuong.java6_asm.dao.OrderItemDAO;
import nhomhoinuong.java6_asm.dto.OrderRequest;
import nhomhoinuong.java6_asm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public Order createOrder(OrderRequest dto) {
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
        if (dto.getItems() != null) {
            for (OrderRequest.OrderItemDTO itemDto : dto.getItems()) {
                OrderItem item = new OrderItem();
                item.setOrderId(savedOrder.getId());
                item.setProductId(itemDto.getProductId());
                item.setQuantity(itemDto.getQuantity());
                item.setPrice(itemDto.getPrice());
                orderItemDAO.save(item);
            }
        }

        // 3. Xóa sạch giỏ hàng của user sau khi đặt thành công
        cartItemDAO.deleteByUserId(dto.getUserId());

        return savedOrder;
    }

    @Override
    public List<Order> getOrders(Long userId, String status) {
        if (userId == null) {
            if (status != null && !status.isEmpty()) {
                return orderDAO.findAllByStatusOrderByOrderDateDesc(status);
            }
            return orderDAO.findAllByOrderByOrderDateDesc();
        }

        if (status != null && !status.isEmpty()) {
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