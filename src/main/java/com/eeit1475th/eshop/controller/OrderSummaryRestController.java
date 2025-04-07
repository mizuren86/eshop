package com.eeit1475th.eshop.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.dto.OrderSummary;
import com.eeit1475th.eshop.cart.service.OrdersService;

@RestController
public class OrderSummaryRestController {

	@Autowired
	private OrdersService ordersService;

	@GetMapping("/api/orderSummary")
	public Map<String, Object> getOrderSummary(
			@RequestParam("userId") Integer userId,
			@RequestParam("shippingMethod") String shippingMethod,
			@RequestParam("selectedCity") String selectedCity,
			@RequestParam("selectedDistrict") String selectedDistrict,
			@RequestParam(value = "couponCode", required = false) String couponCode) {

		// 如果城市或區域未提供，則設定為空字串
		if (selectedCity == null) {
			selectedCity = "";
		}
		if (selectedDistrict == null) {
			selectedDistrict = "";
		}

		// 呼叫後端服務計算訂單摘要，CheckoutService 裡面要針對空地址做處理
		OrderSummary summary = ordersService.calculateOrderSummary(userId, shippingMethod, selectedCity,
				selectedDistrict, couponCode);

		Map<String, Object> result = new HashMap<>();
		result.put("subtotal", summary.getSubtotal());
		result.put("shippingFee", summary.getShippingFee());
		result.put("couponDiscount", summary.getCouponDiscount());
		result.put("grandTotal", summary.getGrandTotal());
		return result;

	}

}
