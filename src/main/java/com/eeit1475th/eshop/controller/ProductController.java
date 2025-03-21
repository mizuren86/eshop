package com.eeit1475th.eshop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeit1475th.eshop.product.entity.ProductCategory;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.service.ProductService;

@Controller
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// Shop 頁面
	@GetMapping("/shop")
	public String shop(@RequestParam(value = "category", required = false) Integer categoryId, Model model) {
		List<ProductCategory> categories = productService.getAllCategories();
		model.addAttribute("categories", categories);
		model.addAttribute("pageTitle", "Shop");

		List<Products> products;
		if (categoryId != null) {
			products = productService.getProductsByCategory(categoryId);
		} else {
			products = productService.getAllProducts();
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
		return productService.searchProducts(keyword);
	}
	
	
}
