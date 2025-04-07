package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderSummary {
    private BigDecimal subtotal;       // 購物車小計
    private BigDecimal shippingFee;    // 運費
    private BigDecimal couponDiscount; // 優惠券折扣
    private BigDecimal grandTotal;     // 合計 = 小計 + 運費 - 優惠券折扣
}