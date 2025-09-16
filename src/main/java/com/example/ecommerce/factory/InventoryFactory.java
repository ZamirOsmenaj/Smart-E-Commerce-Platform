package com.example.ecommerce.factory;

import com.example.ecommerce.domain.Inventory;

import java.util.UUID;

/**
 * Factory responsible for creating {@link Inventory} instances.
 */
public class InventoryFactory {

    public static Inventory createInventoryForProduct(UUID productId, int initialStock) {
        return Inventory.builder()
                .productId(productId)
                .available(initialStock)
                .build();
    }
}
