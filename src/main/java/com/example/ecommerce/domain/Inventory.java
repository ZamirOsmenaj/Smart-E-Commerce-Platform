package com.example.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entity representing the inventory of a product.
 *
 * Tracks the number of units available for a specific product.
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
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
