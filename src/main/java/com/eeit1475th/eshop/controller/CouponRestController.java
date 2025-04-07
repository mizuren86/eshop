package com.eeit1475th.eshop.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.coupon.CouponService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/coupons")
public class CouponRestController {

	@Autowired
	private CouponService couponService;

	/**
	 * 驗證優惠券：前端呼叫時傳入 couponCode，並回傳 JSON 結果 範例回傳格式： { "valid": true,
	 * "discountAmount": 100, "targetAmount": 500, "message": "優惠碼有效" }
	 */
	@GetMapping("/validate")
	public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam("couponCode") String couponCode,
			HttpSession session) {
		Map<String, Object> result = couponService.validateCoupon(couponCode, session);
		return ResponseEntity.ok(result);
	}

	/**
	 * 清除優惠券資料，例如從 Session 中移除優惠券資訊
	 */
	@PostMapping("/clear")
	public ResponseEntity<String> clearCoupon(HttpSession session) {
		couponService.clearCoupon(session);
		return ResponseEntity.ok("OK");
	}

	/**
	 * 儲存優惠券碼到 Session，前端呼叫 /api/coupons/set 會觸發這個方法
	 */
	@PostMapping("/set")
	public ResponseEntity<?> setCouponCode(@RequestBody Map<String, String> payload, HttpSession session) {
		String couponCode = payload.get("couponCode");
		if (couponCode != null && !couponCode.isEmpty()) {
			session.setAttribute("couponCode", couponCode);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().body("couponCode is required");
		}
	}

}
