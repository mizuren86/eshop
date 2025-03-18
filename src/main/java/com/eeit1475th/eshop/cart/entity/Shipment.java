package com.eeit1475th.eshop.cart.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shipment")
public class Shipment {

	@Id
	@Column(name = "shipment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer shipmentId;
	
	@OneToOne
	@JoinColumn(name = "order_id", nullable = false)
	@JsonBackReference
	private Orders orders;
	
	@Column(name = "tracking_number")
	private String trackingNumber;
	
	@Column(name = "carrier")
	private String carrier;
	
	@Column(name = "estimated_delivery")
    @Temporal(TemporalType.TIMESTAMP)
	private Date estimatedDelivery;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "delivery_status", nullable = false)
	private DeliveryStatus deliveryStatus;
	
}
