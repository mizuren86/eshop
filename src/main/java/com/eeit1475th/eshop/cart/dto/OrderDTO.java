package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderDTO {
	
	private Integer orderId;
	
	private LocalDateTime orderDate;
	
	private BigDecimal totalAmount;
	
	private String paymentStatus;
	
	private String shipmentStatus;

	private List<OrderItemDTO> orderItems;

}
