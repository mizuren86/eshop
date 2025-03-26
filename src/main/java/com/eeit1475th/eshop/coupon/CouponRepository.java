package com.eeit1475th.eshop.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Integer>{
	
	Coupon findByCouponCode(String couponCode);
	
}
