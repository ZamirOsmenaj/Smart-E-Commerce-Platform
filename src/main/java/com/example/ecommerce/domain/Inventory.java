package com.example.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

/**
 * Entity representing the inventory of a product.
 *
 * <p>
 * Tracks the number of units available for a specific product.
 * </p>
 */
@Entity
@Table(name = "inventory")
@Data
public class Inventory {

    /**
     * The identifier of the product.
     * Serves as the primary key for inventory records.
     */
    @Id
    private UUID productId;

    /**
     * The number of available units of the product in inventory.
     * Cannot be null.
     */
    @Column(nullable = false)
    private int available;

}
