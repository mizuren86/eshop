package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemDTO {
	
    private Integer productId;
    
    private String productName;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private BigDecimal subTotal;

}
