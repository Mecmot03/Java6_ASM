package nhomhoinuong.java6_asm.bean;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Order_Items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "OrderId", nullable = false)
    private Long orderId;

    @Column(name = "ProductId", nullable = false)
    private Long productId;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "Price", precision = 18, scale = 2)
    private BigDecimal price;
}