package com.example.ecommerce.dto.response;

import com.example.ecommerce.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response payload representing an order.
 *
 * Provides order details.
 */
@Data
@Builder
@AllArgsConstructor
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
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID productId;
        private int quantity;
        private BigDecimal price;
    }
}
