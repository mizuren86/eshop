package com.eeit1475th.eshop.coupon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;

public interface CouponRepository extends JpaRepository<Coupon, Integer>{
	
	List<Coupon> findByCouponId(Integer couponId);
}
