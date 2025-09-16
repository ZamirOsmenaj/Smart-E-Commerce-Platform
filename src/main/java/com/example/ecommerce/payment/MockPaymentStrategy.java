package com.example.ecommerce.payment;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of {@link PaymentStrategy}.
 */
@Component("mockPayment")
public class MockPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResponseDTO processPayment(Order order) {
        // Always succeed for now
        return new PaymentResponseDTO(order.getId(), OrderStatus.PAID);
    }
}
