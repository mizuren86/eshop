package com.eeit1475th.eshop.coupon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon {

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
