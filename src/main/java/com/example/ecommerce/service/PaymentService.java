package com.example.ecommerce.service;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.dto.response.PaymentResponseDTO;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.payment.PaymentStrategy;
import com.example.ecommerce.state.OrderStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for coordinating payment processing
 * using registered {@link PaymentStrategy} implementations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    /**
     * Map of available payment providers, keyed by provider name.
     * Populated automatically by Spring via bean injection.
     */
    private final Map<String, PaymentStrategy> strategies;

    private final OrderService orderService;
    private final OrderStatusPublisher orderStatusPublisher;
    private final OrderStateManager orderStateManager;

    /**
     * Attempts to process payment for the given orderId.
     * If approved -> sets order status to {@link OrderStatus#PAID}.
     * If declined -> sets order status to {@link OrderStatus#CANCELLED} and releases reserved stock.
     *
     * STATE PATTERN: Validates payment operation before processing.
     *
     * @param orderId the ID of the order
     * @param provider the payment provider
     * @return {@link PaymentResponseDTO}
     */
    @Transactional
    public PaymentResponseDTO pay(UUID orderId, String provider) {
        Order order = orderService.findById(orderId);

        // STATE PATTERN: Validate payment operation
        orderStateManager.validateOperation(order, "payment");

        OrderStatus oldStatus = order.getStatus();

        PaymentStrategy strategy = strategies.get(provider);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown payment provider: " + provider);
        }

        PaymentResponseDTO response = strategy.processPayment(order);
        OrderStatus newStatus = response.getOrderStatus();

        // STATE PATTERN: Validate state transition
        orderStateManager.validateTransition(order, newStatus);

        // Update order status through OrderService
        orderService.updateOrderStatus(order, newStatus);

        // Notify observers of status change - this will handle payment failure notifications
        orderStatusPublisher.notifyStatusChange(order, oldStatus, newStatus);

        return response;
    }
}
