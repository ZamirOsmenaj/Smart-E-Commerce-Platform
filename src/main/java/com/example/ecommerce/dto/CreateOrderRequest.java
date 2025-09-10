package com.example.ecommerce.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderRequest {

    private List<Item> items;

    @Data
    public static class Item {
        private UUID productId;
        private int quantity;
    }
}
