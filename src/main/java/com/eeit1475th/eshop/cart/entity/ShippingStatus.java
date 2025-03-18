package com.eeit1475th.eshop.cart.entity;

public enum ShippingStatus {
	
	Processing, Shipped, Delivered, Cancelled;

    public static ShippingStatus fromString(String value) {
        for (ShippingStatus status : ShippingStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("無效的運送狀態: " + value);
    }
	
}
