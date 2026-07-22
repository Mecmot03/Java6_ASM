package nhomhoinuong.java6_asm.bean;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UserId", nullable = false)
    private Long userId;

    @Column(name = "OrderDate")
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "ReceiverName", length = 100)
    private String receiverName;

    @Column(name = "ReceiverPhone", length = 20)
    private String receiverPhone;

    @Column(name = "ShippingAddress", length = 500)
    private String shippingAddress;

    @Column(name = "TotalAmount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "PaymentMethod", length = 50)
    private String paymentMethod;

    @Column(name = "Status", length = 50)
    private String status = "PENDING";
}