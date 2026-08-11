package nhomhoinuong.java6_asm.dto;

import lombok.Data;
//<<<<<<< HEAD
//=======
import java.util.List;
//>>>>>>> main

@Data
public class OrderRequest {
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private String shippingAddress;
    private String note;
    private String paymentMethod;
//<<<<<<< HEAD
//=======
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
    }
//>>>>>>> main
}