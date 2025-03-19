package com.eeit1475th.eshop.product.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
	
	private Integer productId;
	
	private String sku;
	
    private String productName;
    
    private BigDecimal unitPrice;
    
    private String imageUrl;

}
