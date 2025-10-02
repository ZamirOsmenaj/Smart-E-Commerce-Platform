package com.example.ecommerce.service;

import com.example.ecommerce.decorator.EcommerceNotificationService;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.payment.PaymentStrategy;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
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
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderStatusPublisher orderStatusPublisher;
    private final OrderStateManager orderStateManager;
    private final EcommerceNotificationService notificationService;

    /**
     * Attempts to process payment for the given orderId.
     * If approved -> sets order status to PAID.
     * If declined -> sets order status to CANCELLED and releases reserved stock.
     *
     * STATE PATTERN: Validates payment operation before processing.
     *
     * @param orderId
     * @param provider
     *
     * @return {@link PaymentResponseDTO}
     */
    @Transactional
    public PaymentResponseDTO pay(UUID orderId, String provider) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

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

        // Update order status
        order.setStatus(newStatus);
        orderRepository.save(order);

        // Send payment failure notification if payment was declined
        if (newStatus == OrderStatus.CANCELLED) {
            User customer = userRepository.findById(order.getUserId()).orElse(null);
            if (customer != null) {
                log.info("Sending payment failure notification for order {} to user {}", 
                    orderId, customer.getEmail());
                notificationService.sendPaymentFailureNotification(order, customer, "Order has been cancelled :(");
            }
        }

        // Notify observers of status change
        orderStatusPublisher.notifyStatusChange(order, oldStatus, newStatus);

        return response;
    }
}
