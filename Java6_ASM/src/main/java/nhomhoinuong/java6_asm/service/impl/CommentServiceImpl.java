package nhomhoinuong.java6_asm.service.impl;

import nhomhoinuong.java6_asm.bean.Comment;
import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.CommentDAO;
import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.CommentRequest;
import nhomhoinuong.java6_asm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDAO commentDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ProductDAO productDAO;

    @Override
    public List<Comment> getCommentsByProductId(Long productId) {
        return commentDAO.findByProductIdOrderByCreatedAtDesc(productId);
    }

    @Override
    public Comment addComment(CommentRequest request) {
        User user = userDAO.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng ID: " + request.getUserId()));

        Product product = productDAO.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm ID: " + request.getProductId()));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setProduct(product);
        comment.setContent(request.getContent());
        comment.setRating(request.getRating() != null ? request.getRating() : 5);
        comment.setCreatedAt(new Date());

        return commentDAO.save(comment);
    }
}