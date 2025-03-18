package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import com.eeit1475th.eshop.cart.entity.OrderItems;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsDTO {
    private Integer productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;

    public OrderItemsDTO() {}

    public OrderItemsDTO(OrderItems item) {
        this.productId = item.getProducts().getProductId();
        this.productName = item.getProducts().getProductName();
        this.price = item.getPrice();
        this.quantity = item.getQuantity();
    }
}
