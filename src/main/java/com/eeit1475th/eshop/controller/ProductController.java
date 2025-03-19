package com.eeit1475th.eshop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeit1475th.eshop.product.entity.ProductCategory;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.repository.ProductCategoryRepository;
import com.eeit1475th.eshop.product.repository.ProductsRepository;

@Controller
public class ProductController {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductsRepository productsRepository;

    // 透過建構子注入 Repository
    public ProductController(ProductCategoryRepository productCategoryRepository, ProductsRepository productsRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.productsRepository = productsRepository;
    }

    // Shop 頁面
    @GetMapping("/shop")
    public String shop(@RequestParam(value = "category", required = false) Integer categoryId, Model model) {
        List<ProductCategory> categories = productCategoryRepository.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Shop");

        // 如果有選擇分類，就過濾該分類的商品
        List<Products> products;
        if (categoryId != null) {
            products = productsRepository.findByProductCategoryCategoryId(categoryId, null).getContent();
        } else {
            products = productsRepository.findAll();
        }
        model.addAttribute("products", products);

        return "/pages/shop"; // 確保 pages/shop.html 存在
    }

    // Shop Detail 頁面
    @GetMapping("/shop-detail")
    public String shopDetail(Model model) {
        model.addAttribute("pageTitle", "Shop Detail");
        return "/pages/shop-detail";
    }

    // 商品搜尋 API，回傳 JSON
    @GetMapping("/api/products/search")
    @ResponseBody
    public List<Products> searchProducts(@RequestParam("keyword") String keyword) {
        return productsRepository.findByProductNameContaining(keyword, null).getContent();
    }
}
