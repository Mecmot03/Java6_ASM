package nhomhoinuong.java6_asm.model;

import java.math.BigDecimal;
import lombok.Data;

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