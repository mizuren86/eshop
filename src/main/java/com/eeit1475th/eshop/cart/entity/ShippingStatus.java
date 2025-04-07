package com.eeit1475th.eshop.cart.entity;

public enum ShippingStatus {
	
	處理中, 已出貨, 已送達, 已取消;

    public static ShippingStatus fromString(String value) {
        for (ShippingStatus status : ShippingStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("無效的運送狀態: " + value);
    }
	
}


