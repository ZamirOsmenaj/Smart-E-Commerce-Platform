package com.example.ecommerce.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request payload for creating a new order.
 * <p>
 * Contains a list of {@link Item} objects, each representing a product
 * and its desired quantity.
 */
@Data
public class CreateOrderRequestDTO {

    private List<Item> items;

    /**
     * Nested DTO representing a single product within an order creation request.
     */
    @Data
    public static class Item {
        private UUID productId;
        private int quantity;
    }
}
