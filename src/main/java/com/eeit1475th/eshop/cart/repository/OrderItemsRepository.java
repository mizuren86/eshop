package com.eeit1475th.eshop.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.OrderItems;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Integer> {
	
	List<OrderItems> findByOrdersOrderId(Integer orderId);

}
