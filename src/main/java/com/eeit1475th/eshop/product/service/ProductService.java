package com.eeit1475th.eshop.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.product.dto.ProductDTO;
import com.eeit1475th.eshop.product.entity.ProductCategory;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.repository.ProductCategoryRepository;
import com.eeit1475th.eshop.product.repository.ProductsRepository;

@Service
public class ProductService {

    private ProductsRepository productsRepository;
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    public ProductService(ProductsRepository productsRepository, ProductCategoryRepository productCategoryRepository) {
        this.productsRepository = productsRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<Products> getProductsByCategory(Integer categoryId) {
        return productsRepository.findByProductCategoryCategoryId(categoryId, null).getContent();
    }

    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    public List<Products> searchProducts(String keyword) {
        return productsRepository.findByProductNameContaining(keyword, null).getContent();
    }

    public List<ProductCategory> getAllCategories() {
        return productCategoryRepository.findAll();
    }

    public Products getProductById(Integer productId) {
        Optional<Products> product = productsRepository.findById(productId);
        return product.orElse(null); 
    }
    
    
    // 年測試 //
    public ProductDTO getProductDTOById(Integer productId) {
        Products product = getProductById(productId);
        if (product == null) {
            return null;
        }
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setSku(product.getSku());
        dto.setProductName(product.getProductName());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }
    // 年測試 //
    
}
