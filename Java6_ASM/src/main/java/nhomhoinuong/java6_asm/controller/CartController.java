package nhomhoinuong.java6_asm.controller;

import nhomhoinuong.java6_asm.dto.CartItemResponse;
import nhomhoinuong.java6_asm.dto.CartRequest;
import nhomhoinuong.java6_asm.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired
    private CartService cartService;

    // Tạm thời truyền userId qua Header hoặc QueryParam (Hoặc lấy từ Spring Security Principal)
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartCount(@RequestParam Long userId) {
        Map<String, Integer> response = new HashMap<>();
        response.put("count", cartService.getCartCount(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestParam Long userId,
            @RequestBody CartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @RequestParam Long userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(userId, cartItemId, quantity));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestParam Long userId,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(userId, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}