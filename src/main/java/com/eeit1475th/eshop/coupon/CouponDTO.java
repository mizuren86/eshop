package com.eeit1475th.eshop.coupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eeit1475th.eshop.member.dto.UsersDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponDTO {
	private Integer couponId;
	private String couponCode;
	private Integer targetAmount;
	private Integer discountAmount;
	private LocalDateTime expiryDate;
}
