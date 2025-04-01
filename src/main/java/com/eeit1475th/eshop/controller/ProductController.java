package com.eeit1475th.eshop.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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

	private ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// Shop 頁面
	@GetMapping("/shop")
	public String shop(@RequestParam(value = "page", defaultValue = "0") int page,
	                   @RequestParam(value = "size", defaultValue = "5") int size,
	                   @RequestParam(value = "category", required = false) Integer categoryId,
	                   Model model) {

	    // 獲取所有分類
	    List<ProductCategory> categories = productService.getAllCategories();
	    model.addAttribute("categories", categories);

	    // 取得產品清單 (考慮是否有分類篩選)
	    Page<Products> productPage;
	    if (categoryId != null) {
	        productPage = productService.getProductsByCategoryAndPage(categoryId, page, size);
	    } else {
	        productPage = productService.getProductsByPage(page, size);
	    }

	    // 將產品資料與分頁資訊放入 Model
	    model.addAttribute("products", productPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", productPage.getTotalPages());
	    model.addAttribute("selectedCategory", categoryId); // 傳遞目前選擇的分類 (用於前端高亮)

	    return "/pages/shop";
	}


	// Shop Detail 頁面
	@GetMapping("/shop-detail")

	public String shopDetail(@RequestParam("productId") Integer productId, Model model) {

		Products product = productService.getProductById(productId);
		if (product == null) {
			return "redirect:/shop";
		}
		model.addAttribute("product", product);
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
