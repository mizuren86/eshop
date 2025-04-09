package com.eeit1475th.eshop.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	// 返回購物車頁面
	@SuppressWarnings("unchecked")
	@GetMapping
	public String showCart(Model model, HttpSession session,
			@SessionAttribute(value = "user", required = false) Users user) {

		if (user != null) {
		    // 從 session 讀取暫存購物車
		    List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
		    if (tempCart != null && !tempCart.isEmpty()) {
		        // 合併暫存購物車到會員購物車
		        shoppingCartService.mergeTempCartToUserCart(user.getUserId(), tempCart);
		        // 合併後清除 session 中的暫存購物車資料，避免重複合併
		        session.removeAttribute("tempCart");
		    }
		    // 從資料庫讀取會員的購物車資料
		    List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(user.getUserId());
		    model.addAttribute("cartItems", cartItems);
		    model.addAttribute("userId", user.getUserId());
		} else {
		    // 未登入狀態，從 session 讀取暫存購物車
		    List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
		    if (tempCart == null) {
		        tempCart = new ArrayList<>();
		    }
		    model.addAttribute("cartItems", tempCart);
		    model.addAttribute("userId", null);
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
//		BigDecimal couponDiscount = (BigDecimal) session.getAttribute("couponDiscount");
//		if (couponDiscount == null) {
//		    couponDiscount = BigDecimal.ZERO;
//		    session.setAttribute("couponDiscount", couponDiscount);
//		}
//		model.addAttribute("couponDiscount", couponDiscount);

		session.setAttribute("couponDiscount", BigDecimal.ZERO);
		session.removeAttribute("couponCode");

		return "/pages/cart";
	}
}
