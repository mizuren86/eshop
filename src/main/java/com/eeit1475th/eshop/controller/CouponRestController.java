package com.eeit1475th.eshop.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.coupon.Coupon;
import com.eeit1475th.eshop.coupon.CouponRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/coupons")
public class CouponRestController {

	@Autowired
	private CouponRepository couponRepository;

	@GetMapping("/validate")
	public ResponseEntity<Map<String, Object>> validateCoupon(@RequestParam("couponCode") String couponCode, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		// 從資料庫中根據 couponCode 查詢優惠券
		Coupon coupon = couponRepository.findByCouponCode(couponCode);

		if (coupon == null) {
            response.put("valid", false);
            response.put("message", "優惠碼不存在");
            // 清除 session 中優惠券的資料
            session.removeAttribute("couponDiscount");
            return ResponseEntity.ok(response);
        }

		// 檢查優惠券是否過期
        if (coupon.getEndDate().isBefore(LocalDateTime.now())) {
            response.put("valid", false);
            response.put("message", "優惠碼已過期");
            // 清除 session 中優惠券的資料
            session.removeAttribute("couponDiscount");
            return ResponseEntity.ok(response);
        }

     // 驗證成功：回傳折扣金額及目標金額，並將折扣存入 session
        int discount = coupon.getDiscountAmount();
        session.setAttribute("couponDiscount", discount);

        response.put("valid", true);
        response.put("discountAmount", discount);
        response.put("targetAmount", coupon.getTargetAmount());
        response.put("message", "優惠碼有效");
        return ResponseEntity.ok(response);
	}
	
	@PostMapping("/clear")
    public ResponseEntity<String> clearCoupon(HttpSession session) {
        // 清除 session 中優惠券相關的資訊
        session.setAttribute("couponDiscount", 0);
        session.removeAttribute("couponCode");
        return ResponseEntity.ok("OK");
    }

}
