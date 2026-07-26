package nhomhoinuong.java6_asm.service.impl;

import nhomhoinuong.java6_asm.bean.Favorite;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.FavoriteDAO;
import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.FavoriteRequest;
import nhomhoinuong.java6_asm.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteDAO favoriteDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProductDAO productDAO;

    @Override
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteDAO.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public Favorite addFavorite(FavoriteRequest request) {
        if (favoriteDAO.existsByUserIdAndProductId(request.getUserId(), request.getProductId())) {
            return favoriteDAO.findByUserIdAndProductId(request.getUserId(), request.getProductId()).orElse(null);
        }

        User user = userDAO.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + request.getUserId()));

        Product product = productDAO.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + request.getProductId()));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(new Date());

        return favoriteDAO.save(favorite);
    }

    @Override
    public void removeFavorite(Long userId, Long productId) {
        Favorite favorite = favoriteDAO.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin yêu thích"));
        favoriteDAO.delete(favorite);
    }

    @Override
    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteDAO.findByUserId(userId);
    }
}