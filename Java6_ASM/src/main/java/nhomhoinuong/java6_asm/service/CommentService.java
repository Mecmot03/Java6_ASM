package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.bean.Comment;
import nhomhoinuong.java6_asm.dto.CommentRequest;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByProductId(Long productId);
    Comment addComment(CommentRequest request);
}