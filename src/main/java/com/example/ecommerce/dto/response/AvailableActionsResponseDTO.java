package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response DTO for order available actions.
 */
@Data
@Builder
@AllArgsConstructor
public class AvailableActionsResponseDTO {
    private UUID orderId;
    private String actions;
}
