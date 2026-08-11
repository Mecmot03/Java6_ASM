package nhomhoinuong.java6_asm.service.impl;

<<<<<<< HEAD
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
=======
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

>>>>>>> main
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
=======
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
import nhomhoinuong.java6_asm.service.OrderService;
>>>>>>> main

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private OrderItemDAO orderItemDAO;

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
<<<<<<< HEAD
=======
    private ProductDAO productDAO;

    @Autowired
>>>>>>> main
    private UserDAO userDAO;

    @Override
    @Transactional
    public Order createOrder(OrderRequest dto) {
<<<<<<< HEAD
        // 1. Kiểm tra User
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + dto.getUserId()));

        // 2. Lấy danh sách giỏ hàng từ DB
        List<CartItem> cartItems = cartItemDAO.findByUserId(dto.getUserId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng của bạn đang trống!");
        }

        // 3. Khởi tạo Đơn hàng
=======
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể tiến hành đặt hàng!");
        }

        // 1. Khởi tạo đối tượng Order
>>>>>>> main
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

<<<<<<< HEAD
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
=======
        // 2. Lấy giá tiền chuẩn từ CSDL và kiểm tra kho
        for (OrderRequest.OrderItemDTO itemDto : dto.getItems()) {
            Product product = productDAO.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + itemDto.getProductId() + " không tồn tại!"));

            // Kiểm tra tồn kho
            if (product.getQuantity() < itemDto.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho!");
            }

            // Trừ số lượng kho trong CSDL
            product.setQuantity(product.getQuantity() - itemDto.getQuantity());
            productDAO.save(product);

            // Tính tiền bằng đơn giá thực tế trong DB
            BigDecimal itemPrice = product.getPrice();
            BigDecimal subTotal = itemPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            calculatedTotalAmount = calculatedTotalAmount.add(subTotal);

            // Tạo Chi tiết đơn hàng
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(itemPrice);

            orderItemsToSave.add(orderItem);
        }

        // 3. Gán tổng tiền và Lưu Đơn hàng
        order.setTotalAmount(calculatedTotalAmount);
        Order savedOrder = orderDAO.save(order);

        // 4. Lưu từng Chi tiết đơn hàng
        for (OrderItem item : orderItemsToSave) {
            item.setOrderId(savedOrder.getId());
            orderItemDAO.save(item);
        }

        // 5. Xóa giỏ hàng của User
>>>>>>> main
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

    // --- NGHIỆP VỤ MUA LẠI ĐƠN HÀNG (LƯU VÀO CSDL CART_ITEMS) ---
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