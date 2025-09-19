package com.example.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a product available in the system.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product implements Serializable {

    /**
     * serialVersionUID
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier of the product.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The name of the product.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * A description of the product.
     * The field is optional.
     */
    private String description;

    /**
     * The price of the product.
     * Cannot be null.
     */
    @Column(nullable = false)
    private BigDecimal price;
}
