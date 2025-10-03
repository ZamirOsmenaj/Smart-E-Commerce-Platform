package com.example.ecommerce.dto;

import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response payload representing an order.
 * <p>
 * Provides order details.
 */
@Data
@Builder
public class OrderResponseDTO {
    private UUID id;
    private UUID userId;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private Instant createdAt;

    /**
     * Nested DTO representing an item within an {@link OrderResponseDTO}.
     */
    @Data
    @Builder
    public static class OrderItemResponse {
        private UUID productId;
        private int quantity;
        private BigDecimal price;
    }
}
