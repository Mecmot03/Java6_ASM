package nhomhoinuong.java6_asm.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
    }
}