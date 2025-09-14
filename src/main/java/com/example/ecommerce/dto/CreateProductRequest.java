package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Request payload for creating a new product.
 */
@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
}
