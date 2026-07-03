package nhomhoinuong.java6_asm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nhomhoinuong.java6_asm.dao.ProductDAO;
import nhomhoinuong.java6_asm.model.Product;
import nhomhoinuong.java6_asm.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDAO productDAO;

    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productDAO.findById(id);
    }

}