package com.example.ecommerce.dto;

import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderItemResponse> items;

    @Data
    @Builder
    public static class OrderItemResponse {
        private UUID productId;
        private int quantity;
        private BigDecimal price;
    }
}
