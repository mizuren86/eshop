package com.eeit1475th.eshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.coupon.Coupon;
import com.eeit1475th.eshop.coupon.CouponRepository;
import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	/*更改的地方*/
	@Autowired
    private CouponRepository couponrepository;
	/**/

	// 返回購物車頁面 (模板位於 /templates/pages/cart.html)
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
        
        // 假設優惠券折扣固定為 300 儲存在 session
        BigDecimal couponDiscount = new BigDecimal("300");
        session.setAttribute("couponDiscount", couponDiscount);
        model.addAttribute("couponDiscount", couponDiscount);
        
    	/*曾毓皓 更動的地方*/
    	List<Coupon> coupon = couponrepository.findAll();
    	model.addAttribute("coupons", coupon);
    	/**/

		return "/pages/cart";
	}
}
