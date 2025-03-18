package com.eeit1475th.eshop.cart.entity;

public enum PaymentStatus {

	Pending, Paid, Failed, Refunded;

    public static PaymentStatus fromString(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("無效的支付狀態: " + value);
    }
	
}
