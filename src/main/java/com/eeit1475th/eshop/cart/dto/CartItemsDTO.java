package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import com.eeit1475th.eshop.product.dto.ProductDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO {
	
	private Integer cartItemId;
	
    private Integer quantity;
    
    private BigDecimal price;
    
    private BigDecimal subTotal;
    
    private ProductDTO product;

}
