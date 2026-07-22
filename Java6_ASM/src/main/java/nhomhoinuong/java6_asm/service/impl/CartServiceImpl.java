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
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productDAO.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cartItemDAO.findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
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
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized action");
        }

        if (quantity <= 0) {
            cartItemDAO.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        CartItem updated = cartItemDAO.save(cartItem);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void removeFromCart(Long userId, Long cartItemId) {
        CartItem cartItem = cartItemDAO.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

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
                .subTotal(subTotal)
                .build();
    }
}