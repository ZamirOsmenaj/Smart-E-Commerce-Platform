package com.example.ecommerce.factory;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Factory responsible for creating {@link Order} instances.
 */
public class OrderFactory {

    public static Order createNewOrder(UUID userId, BigDecimal total, List<OrderItem> items) {
        return Order.builder()
                .userId(userId)
                .total(total)
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .items(items)
                .build();
    }
}
