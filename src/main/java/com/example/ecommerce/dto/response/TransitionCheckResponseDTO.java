package com.example.ecommerce.dto.response;

import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response DTO for order status transition checks.
 */
@Data
@Builder
public class TransitionCheckResponseDTO {
    private UUID orderId;
    private OrderStatus targetStatus;
    private boolean canTransition;
}