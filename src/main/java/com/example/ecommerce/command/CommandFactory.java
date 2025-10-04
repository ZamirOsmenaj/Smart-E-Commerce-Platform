package com.example.ecommerce.command;

import com.example.ecommerce.command.order.CancelOrderCommand;
import com.example.ecommerce.command.order.CreateOrderCommand;
import com.example.ecommerce.dto.request.CreateOrderRequestDTO;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.proxy.ProductServiceContract;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.InventoryService;
import com.example.ecommerce.service.OrderValidationService;
import com.example.ecommerce.state.OrderStateManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Factory for creating command instances.
 * Centralizes command creation and dependency injection.
 * Commands now contain their own business logic, eliminating cyclic dependencies.
 */
@Component
@RequiredArgsConstructor
public class CommandFactory {
    
    // Direct dependencies for commands
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductServiceContract productService;
    private final OrderValidationService orderValidationService;
    private final OrderStatusPublisher orderStatusPublisher;
    private final OrderStateManager orderStateManager;
    
    /**
     * Creates a command for creating a new order.
     * Command contains complete business logic.
     * 
     * @param userId the user ID
     * @param request the order creation request
     * @return CreateOrderCommand instance
     */
    public CreateOrderCommand createOrderCommand(UUID userId, CreateOrderRequestDTO request) {
        return new CreateOrderCommand(
            orderRepository,
            inventoryService,
            productService,
            orderValidationService,
            orderStatusPublisher,
            userId,
            request
        );
    }
    
    /**
     * Creates a command for cancelling an order.
     * Command contains complete business logic.
     * 
     * @param orderId the order ID to cancel
     * @param reason the cancellation reason
     * @return CancelOrderCommand instance
     */
    public CancelOrderCommand cancelOrderCommand(UUID orderId, String reason) {
        return new CancelOrderCommand(
            orderRepository,
            orderStatusPublisher,
            orderStateManager,
            orderId,
            reason
        );
    }
}