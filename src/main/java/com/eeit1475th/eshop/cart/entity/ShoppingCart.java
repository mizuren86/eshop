package com.eeit1475th.eshop.cart.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.eeit1475th.eshop.member.entity.Users;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "shopping_cart")
public class ShoppingCart {
	
	@Id
	@Column(name = "cart_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer cartId;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private Users users;
	
	@Column(name = "added_at", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime addedAt;
	
	@OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
	@JsonManagedReference(value="shoppingCart-cartItems")
	private List<CartItems> cartItems = new ArrayList<>();

}
