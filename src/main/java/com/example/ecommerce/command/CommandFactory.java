package com.example.ecommerce.command;

//import com.example.ecommerce.command.order.BatchCancelOrdersCommand;
import com.example.ecommerce.command.order.CancelOrderCommand;
import com.example.ecommerce.command.order.CreateOrderCommand;
import com.example.ecommerce.command.order.RefundOrderCommand;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.proxy.ProductServiceInterface;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.InventoryService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.OrderValidationService;
import com.example.ecommerce.state.OrderStateManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Factory for creating command instances.
 * Centralizes command creation and dependency injection.
 * Commands now contain their own business logic, eliminating cyclic dependencies.
 */
@Component
@RequiredArgsConstructor
public class CommandFactory {
    
    // Direct dependencies for commands - no OrderService dependency
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductServiceInterface productService;
    private final OrderValidationService orderValidationService;
    private final OrderStatusPublisher orderStatusPublisher;
    private final OrderStateManager orderStateManager;
    
    // Keep OrderService for batch operations that still need it
//    private final OrderService orderService;
    
    /**
     * Creates a command for creating a new order.
     * Command contains complete business logic - no OrderService dependency.
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
     * Command contains complete business logic - no OrderService dependency.
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
    
    /**
     * Creates a command for processing an order refund.
     * Command contains complete business logic - no OrderService dependency.
     * 
     * @param orderId the order ID to refund
     * @param reason the refund reason
     * @return RefundOrderCommand instance
     */
    public RefundOrderCommand refundOrderCommand(UUID orderId, String reason) {
        return new RefundOrderCommand(
            orderRepository,
            orderStatusPublisher,
            orderStateManager,
            orderId,
            reason
        );
    }
    
//    /**
//     * Creates a command for batch cancellation of orders.
//     *
//     * @param orders the orders to cancel
//     * @param reason the cancellation reason
//     * @param statusPublisher the status publisher for notifications
//     * @return BatchCancelOrdersCommand instance
//     */
//    public BatchCancelOrdersCommand batchCancelOrdersCommand(List<Order> orders, String reason,
//                                                           OrderStatusPublisher statusPublisher) {
//        return new BatchCancelOrdersCommand(orderService, orders, reason, statusPublisher);
//    }
}