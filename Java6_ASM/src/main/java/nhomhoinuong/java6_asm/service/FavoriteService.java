package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.bean.Favorite;
import nhomhoinuong.java6_asm.dto.FavoriteRequest;

import java.util.List;

public interface FavoriteService {
    boolean isFavorite(Long userId, Long productId);
    Favorite addFavorite(FavoriteRequest request);
    void removeFavorite(Long userId, Long productId);
    List<Favorite> getFavoritesByUser(Long userId);
}