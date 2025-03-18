package com.eeit1475th.eshop.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.ShoppingCart;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

	// 根據使用者 ID 查詢對應的購物車
	ShoppingCart findByUsers_UserId(Integer userId);
	
}
