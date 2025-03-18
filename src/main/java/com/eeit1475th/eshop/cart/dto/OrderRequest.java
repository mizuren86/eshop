package com.eeit1475th.eshop.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
	
    private Integer userId;
    
    private String ecPayTradeNo; // 允許為 null

}

