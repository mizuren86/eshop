package com.eeit1475th.eshop.coupon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;

public interface CouponRepository extends JpaRepository<Coupon, Integer>{
	
	Optional<Coupon> findByCouponCode(String couponCode);
}
