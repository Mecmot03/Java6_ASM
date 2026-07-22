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

	@Override
	public List<Product> filterProducts(String keyword, Long categoryId, String brand, String sortBy) {
	    String cleanKeyword = null;
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        // Thay thế dấu '+' từ URL truyền qua thành khoảng trắng chuẩn
	        cleanKeyword = keyword.replace("+", " ").trim();
	        if (cleanKeyword.isEmpty()) {
	            cleanKeyword = null;
	        }
	    }

	    String cleanBrand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : null;
	    String cleanSort = (sortBy != null && !sortBy.trim().isEmpty()) ? sortBy.trim() : null;

	    return productDAO.filterProducts(cleanKeyword, categoryId, cleanBrand, cleanSort);
	}

	@Override
	public Product createProduct(Product product) {
		product.setCreatedAt(LocalDateTime.now());
		product.setUpdatedAt(LocalDateTime.now());
		return productDAO.save(product);
	}

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

	@Override
	public void deleteProduct(Long id) {
		if (productDAO.existsById(id)) {
			productDAO.deleteById(id);
		}
	}
}