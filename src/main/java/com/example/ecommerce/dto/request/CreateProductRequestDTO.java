package com.example.ecommerce.dto.request;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Request payload for creating a new product.
 */
@Data
public class CreateProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private int stock;
}
