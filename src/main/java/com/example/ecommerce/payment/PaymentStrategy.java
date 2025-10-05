package com.example.ecommerce.payment;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.response.PaymentResponseDTO;

/**
 * Common contract for all payment providers.
 *
 * Implementations of this service define how a payment is processed
 * for a given order.
 */
public interface PaymentStrategy {

    /**
     * Process a payment for the given order.
     *
     * @param order the order to process payment for
     * @return {@link PaymentResponseDTO} containing the result of the payment attempt
     */
    PaymentResponseDTO processPayment(Order order);
}
