package nhomhoinuong.java6_asm.bean;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Product {

    private Long id;

    private Long categoryId;

    private Long discountId;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String image;

    private String brand;

    private Boolean status;
}
