package com.eeit1475th.eshop.cart.entity;

import java.math.BigDecimal;

import com.eeit1475th.eshop.product.entity.Products;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItems {

	@Id
	@Column(name ="order_item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderItemId;
	
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	@JsonBackReference
	private Orders orders;
	
	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@JsonBackReference(value = "product-orderItems")
	private Products products;
	
	@Column(name ="quantity")
	private Integer quantity;
	
	@Column(name ="price", precision = 10, scale = 2)
	private BigDecimal price;
	
}
