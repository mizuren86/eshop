package com.eeit1475th.eshop.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
}
