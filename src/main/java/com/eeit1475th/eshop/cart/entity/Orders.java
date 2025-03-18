package com.eeit1475th.eshop.cart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.eeit1475th.eshop.member.entity.Users;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Orders {

	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;

	@Column(name = "order_date", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime orderDate;

	@Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Column(name = "payment_method")
	private String paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;

	@Column(name = "shipping_address")
	private String shippingAddress;

	@Enumerated(EnumType.STRING)
	@Column(name = "shipping_status", nullable = false)
	private ShippingStatus shippingStatus;

	@Column(name = "ec_pay_trade_no", unique = true)
	private String ecPayTradeNo;

	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<OrderItems> orderItems = new ArrayList<>();
	
	@OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Payments payments;
	
	@OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
	@JsonManagedReference
	private Shipment shipment;

}
