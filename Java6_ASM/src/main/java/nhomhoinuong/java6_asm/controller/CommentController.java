package nhomhoinuong.java6_asm.controller;

import nhomhoinuong.java6_asm.bean.Comment;
import nhomhoinuong.java6_asm.dao.CommentDAO;
import nhomhoinuong.java6_asm.dto.CommentRequest;
import nhomhoinuong.java6_asm.dto.ReviewSummaryDTO;
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

    @Autowired
    private CommentDAO commentDAO;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
    }

    // 🔴 API Thống kê sao & số lượng từ CSDL
    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ReviewSummaryDTO> getReviewSummary(@PathVariable Long productId) {
        List<Comment> comments = commentDAO.findByProductIdOrderByCreatedAtDesc(productId);
        long total = comments.size();

        if (total == 0) {
            return ResponseEntity.ok(new ReviewSummaryDTO(5.0, 0L, 0L, 0L, 0L, 0L, 0L));
        }

        double sum = comments.stream().mapToInt(c -> c.getRating() != null ? c.getRating() : 5).sum();
        double avg = Math.round((sum / total) * 10.0) / 10.0;

        long star5 = comments.stream().filter(c -> (c.getRating() != null ? c.getRating() : 5) == 5).count();
        long star4 = comments.stream().filter(c -> (c.getRating() != null ? c.getRating() : 5) == 4).count();
        long star3 = comments.stream().filter(c -> (c.getRating() != null ? c.getRating() : 5) == 3).count();
        long star2 = comments.stream().filter(c -> (c.getRating() != null ? c.getRating() : 5) == 2).count();
        long star1 = comments.stream().filter(c -> (c.getRating() != null ? c.getRating() : 5) == 1).count();

        return ResponseEntity.ok(new ReviewSummaryDTO(avg, total, star5, star4, star3, star2, star1));
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