package nhomhoinuong.java6_asm.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity 
@Table(name = "Products")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @JsonProperty("id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CategoryId", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonProperty("category")
    private Category category;

    @Column(name = "DiscountId")
    @JsonProperty("discountId")
    private Long discountId;

    @Column(name = "Name", nullable = false, length = 255)
    @JsonProperty("name")
    private String name;

    @Column(name = "Description")
    @JsonProperty("description")
    private String description;

    @Column(name = "Price", nullable = false)
    @JsonProperty("price")
    private BigDecimal price;

    @Column(name = "Quantity")
    @JsonProperty("quantity")
    private Integer quantity;

    @Column(name = "Image", length = 255)
    @JsonProperty("image")
    private String image;

    @Column(name = "Brand", length = 100)
    @JsonProperty("brand")
    private String brand;

    @Column(name = "Status")
    @JsonProperty("status")
    private Boolean status;

    @Column(name = "CreatedAt")
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;

    // CONSTRUCTORS
    public Product() {}

    public Product(Long id, Category category, Long discountId, String name, String description, BigDecimal price,
            Integer quantity, String image, String brand, Boolean status, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.category = category;
        this.discountId = discountId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.brand = brand;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // GETTERS & SETTERS THỦ CÔNG (Đảm bảo 100% Jackson đọc được)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Long getDiscountId() { return discountId; }
    public void setDiscountId(Long discountId) { this.discountId = discountId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}