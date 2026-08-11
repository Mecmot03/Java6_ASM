package nhomhoinuong.java6_asm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nhomhoinuong.java6_asm.bean.CartItem;
import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.bean.OrderItem;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.CartItemDAO;
import nhomhoinuong.java6_asm.dao.OrderDAO;
import nhomhoinuong.java6_asm.dao.OrderItemDAO;
import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.OrderRequest;
import nhomhoinuong.java6_asm.service.EmailService;
import nhomhoinuong.java6_asm.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OrderItemDAO orderItemDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Order createOrder(OrderRequest dto) {
        // 1. Kiểm tra User tồn tại
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + dto.getUserId()));

        // 2. Kiểm tra danh sách món hàng trong request
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tiến hành đặt hàng!");
        }

        // 3. Khởi tạo đối tượng Đơn hàng
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setShippingAddress(dto.getShippingAddress());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        // 4. Kiểm tra kho, trừ tồn kho và tính tổng tiền thực tế
        for (OrderRequest.OrderItemDTO itemDto : dto.getItems()) {
            Product product = productDAO.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + itemDto.getProductId() + " không tồn tại!"));

            // Kiểm tra số lượng tồn kho
            if (product.getQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho!");
            }

            // Trừ số lượng tồn kho
            product.setQuantity(product.getQuantity() - itemDto.getQuantity());
            productDAO.save(product);

            // Lấy giá chuẩn từ CSDL
            BigDecimal itemPrice = product.getPrice();
            BigDecimal subTotal = itemPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            calculatedTotalAmount = calculatedTotalAmount.add(subTotal);

            // Tạo Chi tiết đơn hàng tạm thời
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemPrice);

            orderItemsToSave.add(orderItem);
        }

        // 5. Gán tổng tiền và Lưu đơn hàng vào CSDL
        order.setTotalAmount(calculatedTotalAmount);
        Order savedOrder = orderDAO.save(order);

        // 6. Gán orderId vừa tạo cho từng OrderItem và Lưu
        for (OrderItem item : orderItemsToSave) {
            item.setOrderId(savedOrder.getId());
            orderItemDAO.save(item);
        }

        // 7. Dọn sạch giỏ hàng của User trong CSDL
        cartItemDAO.deleteByUserId(dto.getUserId());

        // 8. 🔴 Gửi Email hóa đơn tự động về Email người dùng
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            emailService.sendOrderInvoice(user.getEmail(), savedOrder);
        }

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

    @Override
    @Transactional
    public void rebuyOrder(Long orderId, Long userId) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                Product product = productDAO.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

                Optional<CartItem> existingCartItem = cartItemDAO.findByUserIdAndProductId(userId, item.getProductId());
                if (existingCartItem.isPresent()) {
                    CartItem cartItem = existingCartItem.get();
                    cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                    cartItemDAO.save(cartItem);
                } else {
                    CartItem cartItem = new CartItem();
                    cartItem.setUser(user);
                    cartItem.setProduct(product);
                    cartItem.setQuantity(item.getQuantity());
                    cartItemDAO.save(cartItem);
                }
            }
        }
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