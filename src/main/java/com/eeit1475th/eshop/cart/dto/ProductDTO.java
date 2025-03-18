package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
	
	private Integer productId;
	private String sku;
    private String productName;
    private BigDecimal unitPrice;
    private String imageUrl;

}
