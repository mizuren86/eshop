package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemsDTO {
	
	private Integer cartItemId;
    private Integer quantity;
    private BigDecimal price;
    private ProductDTO product;
    private BigDecimal subTotal;

}
