package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.dto.CartItemResponse;
import nhomhoinuong.java6_asm.dto.CartRequest;

import java.util.List;

public interface CartService {
    List<CartItemResponse> getCartByUser(Long userId);
    CartItemResponse addToCart(Long userId, CartRequest request);
    CartItemResponse updateQuantity(Long userId, Long cartItemId, Integer quantity);
    void removeFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
    Integer getCartCount(Long userId);
}