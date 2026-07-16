package nhomhoinuong.java6_asm.service.impl;

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

}