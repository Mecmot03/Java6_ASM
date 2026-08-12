package nhomhoinuong.java6_asm.service.impl;

import nhomhoinuong.java6_asm.bean.CartItem;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.CartItemDAO;
import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.CartItemResponse;
import nhomhoinuong.java6_asm.dto.CartRequest;
import nhomhoinuong.java6_asm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemDAO cartItemDAO;

    @Autowired
    private ProductDAO productDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public List<CartItemResponse> getCartByUser(Long userId) {
        return cartItemDAO.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartItemResponse addToCart(Long userId, CartRequest request) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Product product = productDAO.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        int availableStock = product.getQuantity() != null ? product.getQuantity() : 0;
        if (availableStock <= 0) {
            throw new RuntimeException("Sản phẩm này tạm thời đã hết hàng!");
        }

        CartItem cartItem = cartItemDAO.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        int currentQuantityInCart = (cartItem != null) ? cartItem.getQuantity() : 0;
        int targetQuantity = currentQuantityInCart + request.getQuantity();

        // 🔴 KIỂM TRA TỒN KHO KHI THÊM VÀO GIỎ
        if (targetQuantity > availableStock) {
            throw new RuntimeException("Rất tiếc, kho chỉ còn " + availableStock + " sản phẩm! Bạn đã có " + currentQuantityInCart + " sản phẩm trong giỏ hàng.");
        }

        if (cartItem != null) {
            cartItem.setQuantity(targetQuantity);
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        }

        CartItem saved = cartItemDAO.save(cartItem);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer quantity) {
        CartItem cartItem = cartItemDAO.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Hành động không hợp lệ");
        }

        if (quantity <= 0) {
            cartItemDAO.delete(cartItem);
            return null;
        }

        // 🔴 KIỂM TRA TỒN KHO KHI TĂNG SỐ LƯỢNG TRONG TRANG GIỎ HÀNG
        Product product = cartItem.getProduct();
        int availableStock = product.getQuantity() != null ? product.getQuantity() : 0;
        if (quantity > availableStock) {
            throw new RuntimeException("Không thể tăng! Kho chỉ còn tối đa " + availableStock + " sản phẩm.");
        }

        cartItem.setQuantity(quantity);
        CartItem updated = cartItemDAO.save(cartItem);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemDAO.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ"));

        if (cartItem.getUser().getId().equals(userId)) {
            cartItemDAO.delete(cartItem);
        }
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartItemDAO.deleteByUserId(userId);
    }

    @Override
    public Integer getCartCount(Long userId) {
        return cartItemDAO.countTotalItemsByUserId(userId);
    }

   private CartItemResponse mapToResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal price = product.getPrice();
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImage())
                .price(price)
                .quantity(cartItem.getQuantity())
                .stock(product.getQuantity() != null ? product.getQuantity() : 0) // 🔴 Lấy số lượng tồn kho từ CSDL
                .subTotal(subTotal)
                .build();
    }
}