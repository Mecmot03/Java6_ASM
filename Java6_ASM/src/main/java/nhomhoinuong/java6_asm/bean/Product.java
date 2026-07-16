package nhomhoinuong.java6_asm.bean;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id; 
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table(name = "Products")
@Data
public class Product {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CategoryId", referencedColumnName = "Id", nullable = false) 
    private Category category;

    @Column(name = "DiscountId")
    private Long discountId; 

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String image;

    private String brand;

    private Boolean status;
}