package com.example.ecommerce.command.order;

import com.example.ecommerce.command.Command;
import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.mapper.MapperFacade;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.state.OrderStateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Command for cancelling an order.
 * Contains the complete order cancellation business logic.
 * Note: Undo for cancellation is complex and typically not supported in real systems.
 */
@RequiredArgsConstructor
@Slf4j
public class CancelOrderCommand implements Command {
    
    // Direct dependencies - no OrderService needed
    private final OrderRepository orderRepository;
    private final OrderStatusPublisher orderStatusPublisher;
    private final OrderStateManager orderStateManager;
    
    // Command parameters
    private final UUID orderId;
    private final String reason;

    // Store original order state for potential undo
    private OrderStatus originalStatus;

    // Constructor for cases where OrderStateManager is not needed (like undo operations)
    public CancelOrderCommand(OrderRepository orderRepository, 
                            OrderStatusPublisher orderStatusPublisher,
                            UUID orderId, 
                            String reason) {
        this.orderRepository = orderRepository;
        this.orderStatusPublisher = orderStatusPublisher;
        this.orderStateManager = null; // Will skip state validation
        this.orderId = orderId;
        this.reason = reason;
    }
    
    @Override
    @Transactional
    public CommandResult execute() throws Exception {
        try {
            log.info("COMMAND: Executing CancelOrderCommand for order: {} with reason: {}", orderId, reason);
            
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found!"));
            
            log.info("COMMAND: Found order {} with current status: {}", orderId, order.getStatus());
            this.originalStatus = order.getStatus();
            
            // STATE PATTERN: Validate cancellation operation (if state manager is available)
            if (orderStateManager != null) {
                orderStateManager.validateOperation(order, "cancel");
                orderStateManager.validateTransition(order, OrderStatus.CANCELLED);
            }
            
            OrderStatus oldStatus = order.getStatus();
            
            // For paid orders, we would need to process refund here
            // For now, we'll just change the status
            order.setStatus(OrderStatus.CANCELLED);
            
            // Save the updated order
            order = orderRepository.save(order);
            
            // OBSERVER PATTERN: Notify observers of status change
            orderStatusPublisher.notifyStatusChange(order, oldStatus, OrderStatus.CANCELLED);
            
            log.info("COMMAND: Successfully cancelled order: {} (was: {}, now: {})", 
                    orderId, originalStatus, order.getStatus());
            
            OrderResponseDTO response = MapperFacade.toResponseDTO(order);
            return CommandResult.success("Order cancelled successfully", response);
            
        } catch (IllegalStateException e) {
            log.warn("COMMAND: Cannot cancel order: {} - State validation failed: {}", orderId, e.getMessage());
            return CommandResult.failure("Cannot cancel order: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("COMMAND: Failed to cancel order: {} - Error: {}", orderId, e.getMessage());
            return CommandResult.failure("Failed to cancel order: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CommandResult undo() throws Exception {
        // In a real system, undoing a cancellation is complex and often not allowed
        // It would require:
        // 1. Checking if inventory is still available
        // 2. Re-reserving stock
        // 3. Potentially re-processing payment
        // 4. Updating all related systems
        
        log.warn("COMMAND: Undo requested for CancelOrderCommand - this operation is not supported");
        return CommandResult.failure("Undoing order cancellation is not supported for business reasons");
    }
    
    @Override
    public boolean supportsUndo() {
        return false; // Cancellation undo is typically not supported
    }
    
    @Override
    public String getDescription() {
        return String.format("Cancel order: %s with reason: %s", orderId, reason);
    }
}