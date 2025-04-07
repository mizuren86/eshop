package com.eeit1475th.eshop.cart.entity;

public enum DeliveryStatus {

	待處理, 運送中, 已送達;

    public static ShippingStatus fromString(String value) {
        for (ShippingStatus status : ShippingStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("無效的運送狀態: " + value);
    }
	
}
