package com.example.ecommerce.dto.request;

import lombok.Data;

/**
 * Request DTO for order cancellation.
 */
@Data
public class CancellationRequestDTO {
    private String reason;
}