package com.example.ecommerce.service;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import com.example.ecommerce.payment.PaymentStrategy;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
public class PaymentService {

    /**
     * Map of available payment providers, keyed by provider name.
     * Populated automatically by Spring via bean injection.
     */
    private final Map<String, PaymentStrategy> strategies;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    /**
     * Attempts to process payment for the given orderId.
     * If approved -> sets order status to PAID.
     * If declined -> sets order status to CANCELLED and releases reserved stock.
     *
     * @param orderId
     *
     * @return true if payment approved.
     */
    @Transactional
    public PaymentResponseDTO pay(UUID orderId, String provider) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in PENDING state!");
        }

        PaymentStrategy strategy = strategies.get(provider);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown payment provider: " + provider);
        }

        PaymentResponseDTO response = strategy.processPayment(order);

        if ("PAID".equalsIgnoreCase(response.getOrderStatus().toString())) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        } else {
            // payment declined => cancel order and release reserved stock
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }
        }

        return response;
    }

}
