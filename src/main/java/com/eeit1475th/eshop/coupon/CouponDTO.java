package com.eeit1475th.eshop.coupon;

import java.math.BigDecimal;

import com.eeit1475th.eshop.member.dto.UsersDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponDTO {
	private Integer Coupon_id;
    private UsersDTO Users;
    private Integer Coupon_discount;
}
