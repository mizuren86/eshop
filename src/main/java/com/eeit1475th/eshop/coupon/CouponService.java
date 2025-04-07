package com.eeit1475th.eshop.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class CouponService {

	@Autowired
	private CouponRepository couponRepository;

	/**
	 * 驗證優惠券：檢查優惠碼是否存在以及是否過期，並將折扣金額與優惠碼存入 HttpSession
	 * 
	 * @param couponCode 前端傳入的優惠碼
	 * @param session    HttpSession 用來儲存優惠券資訊
	 * @return 一個 Map，包含驗證結果、折扣金額、目標金額以及提示訊息
	 */
	public Map<String, Object> validateCoupon(String couponCode, HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		// 從資料庫中根據 couponCode 查詢優惠券
		Coupon coupon = couponRepository.findByCouponCode(couponCode);
		if (coupon == null) {
			response.put("valid", false);
			response.put("message", "優惠碼不存在");
			session.removeAttribute("couponDiscount");
			session.removeAttribute("couponCode");
			return response;
		}

		// 檢查優惠券是否過期
		if (coupon.getEndDate().isBefore(LocalDateTime.now())) {
			response.put("valid", false);
			response.put("message", "優惠碼已過期");
			session.removeAttribute("couponDiscount");
			session.removeAttribute("couponCode");
			return response;
		}

		// 優惠券驗證通過，將折扣金額與優惠碼存入 session
		int discount = coupon.getDiscountAmount();
		session.setAttribute("couponDiscount", discount);
		session.setAttribute("couponCode", couponCode);

		response.put("valid", true);
		response.put("discountAmount", discount);
		response.put("targetAmount", coupon.getTargetAmount());
		response.put("message", "優惠碼有效");

		return response;
	}

	/**
	 * 清除 session 中優惠券相關資訊
	 * 
	 * @param session
	 */
	public void clearCoupon(HttpSession session) {
		session.removeAttribute("couponDiscount");
		session.removeAttribute("couponCode");
	}
	
	/**
     * 根據優惠碼取得折扣金額
     * 
     * @param couponCode 優惠碼（前端傳入）
     * @return 如果優惠碼存在且未過期，回傳折扣金額，否則回傳 0
     */
    public BigDecimal getDiscountByCouponCode(String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        Coupon coupon = couponRepository.findByCouponCode(couponCode.trim());
        if (coupon == null || coupon.getEndDate().isBefore(LocalDateTime.now())) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(coupon.getDiscountAmount());
    }

}
