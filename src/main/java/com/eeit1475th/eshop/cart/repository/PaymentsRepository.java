package com.eeit1475th.eshop.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.Payments;

public interface PaymentsRepository extends JpaRepository<Payments, Integer> {

	Payments findByOrders_OrderId(Integer orderId);
	
}
