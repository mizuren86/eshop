package com.eeit1475th.eshop.cart.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationDTO {
    
    // 產品所屬分類 id
    private Integer categoryId;
    
    private String sku;
    private String productName;
    private String description;
    private BigDecimal unitPrice;
    private String imageUrl;
    
    // 是否上架
    private boolean active;
    
    // 庫存數量
    private Integer unitInStock;
}
