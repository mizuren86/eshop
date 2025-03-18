package com.eeit1475th.eshop.cart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.cart.entity.Shipment;
import com.eeit1475th.eshop.cart.repository.ShipmentRepository;

@Service
public class ShipmentService {
	
	@Autowired
	private ShipmentRepository shipmentRepository;
	
	// 根據訂單查詢運送記錄
    public List<Shipment> getShipmentByOrderId(Integer orderId) {
        return shipmentRepository.findByOrders_OrderId(orderId);
    }

    // 創建運送記錄
    public Shipment createShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

}
