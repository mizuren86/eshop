package com.eeit1475th.eshop.cart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eeit1475th.eshop.member.entity.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payments {

	@Id
	@Column(name = "payment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer paymentId;

	@OneToOne
	@JoinColumn(name = "order_id", nullable = false)
	@JsonBackReference
	private Orders orders;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users users;

	@Column(name = "payment_date", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime paymentDate;

	@Column(name = "amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(name = "payment_method")
	private String paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;

	@Column(name = "ec_pay_trade_no", unique = true)
	private String ecPayTradeNo;

}
