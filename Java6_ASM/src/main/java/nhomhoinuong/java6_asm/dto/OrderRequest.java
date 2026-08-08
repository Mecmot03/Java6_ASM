package nhomhoinuong.java6_asm.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String paymentMethod;
}