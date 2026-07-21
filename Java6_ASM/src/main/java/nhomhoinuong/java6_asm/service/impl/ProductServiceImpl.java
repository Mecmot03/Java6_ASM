package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import nhomhoinuong.java6_asm.bean.Product;
import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.service.ProductService;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productDAO.findById(id).orElse(null);
    }

    // =========================
    // Thêm
    // =========================

    @Override
    public Product createProduct(Product product) {

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return productDAO.save(product);

    }

    // =========================
    // Sửa
    // =========================

    @Override
    public Product updateProduct(Long id, Product product) {

        Product oldProduct = productDAO.findById(id).orElse(null);

        if (oldProduct == null) {
            return null;
        }

        oldProduct.setCategory(product.getCategory());
        oldProduct.setDiscountId(product.getDiscountId());
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setQuantity(product.getQuantity());
        oldProduct.setImage(product.getImage());
        oldProduct.setBrand(product.getBrand());
        oldProduct.setStatus(product.getStatus());

        oldProduct.setUpdatedAt(LocalDateTime.now());

        return productDAO.save(oldProduct);

    }

    // =========================
    // Xóa
    // =========================

    @Override
    public void deleteProduct(Long id) {

        if (productDAO.existsById(id)) {
            productDAO.deleteById(id);
        }

    }

}