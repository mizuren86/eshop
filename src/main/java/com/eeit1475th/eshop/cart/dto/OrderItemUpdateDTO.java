package com.eeit1475th.eshop.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemUpdateDTO {
    private Integer productId;
    private Integer quantity;
}
