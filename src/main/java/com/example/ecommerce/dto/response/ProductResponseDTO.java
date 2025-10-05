package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for product information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    
    /**
     * The unique identifier of the product.
     */
    private UUID id;
    
    /**
     * The name of the product.
     */
    private String name;
    
    /**
     * A description of the product.
     */
    private String description;
    
    /**
     * The price of the product.
     */
    private BigDecimal price;
}