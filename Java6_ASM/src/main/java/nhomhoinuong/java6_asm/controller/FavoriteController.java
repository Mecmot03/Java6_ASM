package nhomhoinuong.java6_asm.controller;

import nhomhoinuong.java6_asm.bean.Favorite;
import nhomhoinuong.java6_asm.dto.FavoriteRequest;
import nhomhoinuong.java6_asm.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin("*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Kiểm tra đã thích chưa
    @GetMapping("/check")
    public ResponseEntity<?> checkFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        boolean isFav = favoriteService.isFavorite(userId, productId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFav);
        return ResponseEntity.ok(response);
    }

    // Thêm vào danh sách yêu thích
    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            Favorite favorite = favoriteService.addFavorite(request);
            return ResponseEntity.ok(favorite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Bỏ yêu thích
    @DeleteMapping
    public ResponseEntity<?> removeFavorite(@RequestParam Long userId, @RequestParam Long productId) {
        try {
            favoriteService.removeFavorite(userId, productId);
            return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
	// Lấy danh sách yêu thích của người dùng
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFavoritesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUser(userId));
    }
}