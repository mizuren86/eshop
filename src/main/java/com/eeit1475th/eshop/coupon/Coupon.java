package com.eeit1475th.eshop.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.eeit1475th.eshop.cart.entity.OrderItems;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.entity.Payments;
import com.eeit1475th.eshop.cart.entity.Shipment;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.member.entity.Users;

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
@Table(name="coupon")
public class Coupon {
	
//	@Id
//	@Column(name = "coupon_id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Integer couponId;
//	
//	@ManyToOne
//	@JoinColumn(name = "users_id", nullable = false)
//	private Users users;
//	
//	@JoinColumn(name = "coupon_discount", nullable = false)
//	private Integer coupon_discount;
    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Column(name = "target_amount", nullable = false)
    private Integer targetAmount;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

	
}
