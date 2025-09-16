package com.example.ecommerce.factory;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Factory responsible for creating {@link OrderItem} instances.
 */
public class OrderItemFactory {

    public static OrderItem createNewOrderItem(Order order, UUID productId, int quantity, BigDecimal price) {
        return OrderItem.builder()
                .order(order)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
