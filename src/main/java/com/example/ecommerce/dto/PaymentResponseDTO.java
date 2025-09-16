package com.example.ecommerce.dto;

import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Response payload of a payment attempt.
 */
@Data
@AllArgsConstructor
public class PaymentResponseDTO {

    /**
     * Unique identifier of the order associated with the payment.
     */
    UUID orderId;

    /**
     * Current status of the order after processing the payment.
     */
    OrderStatus orderStatus;
}
