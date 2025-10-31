package com.ads.product.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private BigDecimal price;
    private Integer stock;
    private Long categoryId;      
    private String categoryName;  
}
