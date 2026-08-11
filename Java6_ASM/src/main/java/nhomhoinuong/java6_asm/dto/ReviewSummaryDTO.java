package nhomhoinuong.java6_asm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSummaryDTO {
    private Double averageRating;
    private Long totalReviews;
    private Long count5Star;
    private Long count4Star;
    private Long count3Star;
    private Long count2Star;
    private Long count1Star;
}