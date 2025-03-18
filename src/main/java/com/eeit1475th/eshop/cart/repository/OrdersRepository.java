package com.eeit1475th.eshop.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	List<Orders> findByUsers_UserId(Integer userId);
	
	Optional<Orders> findByEcPayTradeNo(String tradeNo);
	
}
