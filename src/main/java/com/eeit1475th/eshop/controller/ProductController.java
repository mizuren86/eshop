package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

	// Shop 頁面
    @GetMapping("/shop")
    public String shop(Model model) {
        model.addAttribute("pageTitle", "Shop");
        // 對應 templates/pages/shop.html
        return "/pages/shop";
    }

    // Shop Detail 頁面
    @GetMapping("/shop-detail")
    public String shopDetail(Model model) {
        model.addAttribute("pageTitle", "Shop Detail");
        // 對應 templates/pages/shop-detail.html
        return "/pages/shop-detail";
    }
	
}
