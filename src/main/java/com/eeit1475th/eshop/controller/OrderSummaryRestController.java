package com.eeit1475th.eshop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.dto.OrderSummary;
import com.eeit1475th.eshop.cart.service.OrdersService;

import jakarta.servlet.http.HttpSession;

@RestController
public class OrderSummaryRestController {

	@Autowired
	private OrdersService ordersService;

	@GetMapping("/api/orderSummary")
	public Map<String, Object> getOrderSummary(
			@RequestParam(value = "userId", required = false) Integer userId,
			@RequestParam("shippingMethod") String shippingMethod,
			@RequestParam("selectedCity") String selectedCity,
			@RequestParam("selectedDistrict") String selectedDistrict,
			@RequestParam(value = "couponCode", required = false) String couponCode,
			HttpSession session) {

		// 如果城市或區域未提供，則設定為空字串
		if (selectedCity == null) {
			selectedCity = "";
		}
		if (selectedDistrict == null) {
			selectedDistrict = "";
		}

		OrderSummary summary;
	    if (userId == null || userId == 0) {
	        // 未登入狀態：從 session 讀取暫存購物車資料進行計算
	        List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
	        if (tempCart == null) {
	            tempCart = new ArrayList<>();
	        }
	        summary = ordersService.calculateOrderSummaryForCartItems(tempCart, shippingMethod, selectedCity, selectedDistrict, couponCode);
	    } else {
	        // 登入狀態：直接從資料庫取得會員購物車資料計算訂單摘要
	        summary = ordersService.calculateOrderSummary(userId, shippingMethod, selectedCity, selectedDistrict, couponCode);
	    }

		Map<String, Object> result = new HashMap<>();
		result.put("subtotal", summary.getSubtotal());
		result.put("shippingFee", summary.getShippingFee());
		result.put("couponDiscount", summary.getCouponDiscount());
		result.put("grandTotal", summary.getGrandTotal());
		return result;

	}

}
