package com.example.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Product Data Transfer Object
 * 
 * This DTO matches the Product entity from Product Service.
 * Used for Feign client communication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean available;
    private String category;
}
