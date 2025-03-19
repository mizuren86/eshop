package com.eeit1475th.eshop.member.entity;

import java.util.ArrayList;
import java.util.List;

import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.Payments;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.review.Reviews;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "users")
public class Users {

	@Id
	@Column(name ="user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;
	
	@Column(name ="username")
	private String username;
	
	@Column(name ="password")
	private String password;
	
	@Column(name ="email")
	private String email;
	
	@Column(name ="full_name")
	private String fullName;
	
	@Column(name ="phone")
	private String phone;
	
	@Column(name ="user_photo")
	private String userPhoto;
	
	@Column(name ="address")
	private String address;
	
	@OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
	@JsonManagedReference
	private ShoppingCart shoppingCart;
	
	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Orders> orders = new ArrayList<>();
	
	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private List<Payments> payments = new ArrayList<>();
			
	@JsonManagedReference
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    private UserVip userVip;

    @JsonManagedReference
    @OneToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<UserVipHistory> vipHistories;
    
    @OneToMany(mappedBy = "reviews", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-reviews")
    private List<Reviews> userReviews;
	
}
