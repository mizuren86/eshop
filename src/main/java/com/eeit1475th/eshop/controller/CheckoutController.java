package com.eeit1475th.eshop.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	@GetMapping("/checkout")
	public String showCheckoutPage(Model model, HttpSession session,
			@RequestParam(value = "shippingMethod", required = false) String shippingMethod,
			@RequestParam(value = "paymentMethod", required = false) String paymentMethod) {

		// 取得使用者ID與會員資料（假設已存入 session）
		Integer userId = (Integer) session.getAttribute("userId");
		Users user = (Users) session.getAttribute("user");
		if (user == null) {
			return "redirect:/login?redirect=/checkout";
		}
		model.addAttribute("userId", userId);
		model.addAttribute("users", user);

		// 取得該使用者購物車商品列表
		List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(userId);
		model.addAttribute("cartItems", cartItems);

		// 從 Session 讀取優惠券折扣，若沒有則預設為 0
		// 如果優惠券資料是數字（BigDecimal），也可以這樣處理：
		Object couponObj = session.getAttribute("couponDiscount");
		BigDecimal couponDiscount;
		if (couponObj instanceof BigDecimal) {
			couponDiscount = (BigDecimal) couponObj;
		} else {
			// 若 Session 中沒有，則預設為 0
			couponDiscount = BigDecimal.ZERO;
			session.setAttribute("couponDiscount", couponDiscount);
		}
		model.addAttribute("couponDiscount", couponDiscount);

		// 同理，若你有優惠券代碼（couponCode）
		String couponCode = (String) session.getAttribute("couponCode");
		if (couponCode == null) {
			couponCode = "";
			session.setAttribute("couponCode", couponCode);
		}
		model.addAttribute("couponCode", couponCode);

		// 計算購物車小計
		BigDecimal totalAmount = cartItems.stream().map(CartItemsDTO::getSubTotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);
		model.addAttribute("totalAmount", totalAmount);

		// 儲存或讀取運送方式：
		if (shippingMethod != null && !shippingMethod.trim().isEmpty()) {
			session.setAttribute("shippingMethod", shippingMethod.trim());
		} else if (session.getAttribute("shippingMethod") == null) {
			session.setAttribute("shippingMethod", "711-cod"); // 預設值
		}
		String sessionShippingMethod = (String) session.getAttribute("shippingMethod");
		model.addAttribute("shippingMethod", sessionShippingMethod);

		// 儲存或讀取付款方式：
		if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
			session.setAttribute("paymentMethod", paymentMethod.trim());
		} else if (session.getAttribute("paymentMethod") == null) {
			session.setAttribute("paymentMethod", "711-cod-only");
		}
		String sessionPaymentMethod = (String) session.getAttribute("paymentMethod");
		model.addAttribute("paymentMethod", sessionPaymentMethod);

		return "/pages/checkout";
	}
}
