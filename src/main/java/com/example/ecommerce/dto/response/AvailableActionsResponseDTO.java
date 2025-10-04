package com.example.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response DTO for order available actions.
 */
@Data
@Builder
public class AvailableActionsResponseDTO {
    private UUID orderId;
    private String actions;
}
