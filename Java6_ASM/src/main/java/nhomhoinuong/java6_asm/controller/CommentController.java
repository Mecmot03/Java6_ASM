package nhomhoinuong.java6_asm.controller;

import nhomhoinuong.java6_asm.bean.Comment;
import nhomhoinuong.java6_asm.dto.CommentRequest;
import nhomhoinuong.java6_asm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin("*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentRequest request) {
        try {
            Comment comment = commentService.addComment(request);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}