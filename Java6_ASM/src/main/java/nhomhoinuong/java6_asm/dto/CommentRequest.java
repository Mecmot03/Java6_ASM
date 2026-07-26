package nhomhoinuong.java6_asm.dto;

import lombok.Data;

@Data
public class CommentRequest {
    private Long userId;
    private Long productId;
    private String content;
    private Integer rating;
}