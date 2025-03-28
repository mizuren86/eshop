package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	// 返回購物車頁面
	@GetMapping
	public String showCart(Model model, HttpSession session,
			@SessionAttribute(value = "user", required = false) Users user) {
		
		if (user == null) {
			user = new Users();
			user.setUserId(3);
			session.setAttribute("user", user);
		}
		
		// 從 session 讀取用戶先前選擇的運送與付款方式
	    String shippingMethod = (String) session.getAttribute("shippingMethod");
	    String paymentMethod = (String) session.getAttribute("paymentMethod");
	    
	    if (shippingMethod == null) {
	        shippingMethod = "711-cod";
	        session.setAttribute("shippingMethod", shippingMethod);
	    }
	    if (paymentMethod == null) {
	        paymentMethod = "711-cod-only";
	        session.setAttribute("paymentMethod", paymentMethod);
	    }
	    String sessionShippingMethod = (String) session.getAttribute("shippingMethod");
        String sessionPaymentMethod = (String) session.getAttribute("paymentMethod");
        model.addAttribute("shippingMethod", sessionShippingMethod);
        model.addAttribute("paymentMethod", sessionPaymentMethod);
        
        // 從 session 讀取優惠券折扣，若不存在則預設為 0
        Integer couponDiscount = (Integer) session.getAttribute("couponDiscount");
        if (couponDiscount == null) {
            couponDiscount = 0;
            session.setAttribute("couponDiscount", couponDiscount);
        }
        model.addAttribute("couponDiscount", couponDiscount);
        
		return "/pages/cart";
	}
}
