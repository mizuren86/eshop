package com.eeit1475th.eshop.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eeit1475th.eshop.cart.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

	List<Shipment> findByOrders_OrderId(Integer orderId);
	
}
