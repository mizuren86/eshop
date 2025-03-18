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
public class OrdersDTO {

	private Integer orderId;
	private Integer userId;
	private LocalDateTime orderDate;
	private BigDecimal totalAmount;
	private String paymentStatus;

	private List<OrderItemsDTO> orderItems;

	public OrdersDTO(Integer orderId, Integer userId, LocalDateTime orderDate, BigDecimal totalAmount,
			String paymentStatus) {
		this.orderId = orderId;
		this.userId = userId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
	}

	public OrdersDTO(Integer orderId, Integer userId, LocalDateTime orderDate, BigDecimal totalAmount,
			String paymentStatus, List<OrderItemsDTO> orderItems) {
		this.orderId = orderId;
		this.userId = userId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.orderItems = orderItems;
	}

}
