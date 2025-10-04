package com.example.ecommerce.service;

import com.example.ecommerce.command.CommandFactory;
import com.example.ecommerce.command.CommandInvoker;
import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.dto.request.CreateOrderRequestDTO;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.state.OrderStateManager;
import com.example.ecommerce.utils.OrderMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service responsible for handling customer orders.
 * Integrates Command Pattern for advanced operations with undo capabilities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStateManager orderStateManager;
    private final OrderStatusPublisher statusPublisher;

    // Command Pattern components - direct injection now that there's no cyclic dependency
    private final CommandFactory commandFactory;
    private final CommandInvoker commandInvoker;

    /**
     * Retrieves specific order based on the order ID for that user
     */
    @Transactional(readOnly = true)
    public OrderResponseDTO getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(OrderMapperUtils::toResponse)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the ID of the user
     *
     * @return a list of {@link OrderResponseDTO} representing the user's orders
     */
    public List<OrderResponseDTO> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderMapperUtils::toResponse)
                .toList();
    }

    /**
     * Updates the status of an order.
     * This method encapsulates order status updates within the OrderService.
     */
    @Transactional
    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    /**
     * Retrieves specific order entity based on the order ID.
     * This method is used internally by other components that need the full Order entity.
     *
     * @param orderId the ID of the order to find
     * @return the Order entity
     * @throws RuntimeException if the order is not found
     */
    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    /**
     * Saves an order entity.
     * This method encapsulates order persistence within the OrderService.
     *
     * @param order the order to save
     * @return the saved order entity
     */
    @Transactional
    public void save(Order order) {
        orderRepository.save(order);
    }

    /**
     * Finds orders by status and created before a specific time.
     * This method encapsulates repository access and provides a clean service layer interface.
     *
     * @param status the order status to filter by
     * @param cutoffTime orders created before this time
     *
     * @return list of orders matching the criteria
     */
    public List<Order> findOrdersByStatusAndCreatedBefore(OrderStatus status,
                                                          Instant cutoffTime) {
        log.debug("Finding orders with status {} created before {}", status, cutoffTime);
        return orderRepository.findByStatusAndCreatedAtBefore(status, cutoffTime);
    }

    /**
     * Bulk cancellation of orders with status change notification.
     * This method handles the complete cancellation workflow including observer notifications.
     *
     * @param orders list of orders to cancel
     * @param reason the reason for cancellation
     */
    @Transactional
    public void cancelOrders(List<Order> orders, String reason) {
        log.info("Bulk cancelling {} orders - Reason: {}", orders.size(), reason);
        
        for (Order order : orders) {
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            
            // Save the order
            orderRepository.save(order);
            
            // Notify observers (this will handle inventory release, notifications, etc.)
            statusPublisher.notifyStatusChange(order, oldStatus, OrderStatus.CANCELLED);
            
            log.debug("Cancelled order: {}", order.getId());
        }
        
        log.info("Bulk cancellation completed for {} orders", orders.size());
    }

    /**
     * Finds unpaid orders that are older than the specified time.
     * Business logic method that encapsulates the criteria for "unpaid orders".
     *
     * @param cutoffTime orders created before this time are considered stale
     *
     * @return list of unpaid orders that should be cancelled
     */
    public List<Order> findUnpaidOrdersOlderThan(Instant cutoffTime) {
        log.debug("Finding unpaid orders older than {}", cutoffTime);
        return findOrdersByStatusAndCreatedBefore(OrderStatus.PENDING, cutoffTime);
    }
    
    /**
     * Gets available actions for an order in its current state.
     * 
     * @param orderId the ID of the order
     * @return description of available actions
     */
    public String getOrderAvailableActions(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        
        return orderStateManager.getAvailableActions(order);
    }
    
    /**
     * Checks if an order can transition to a specific status.
     * 
     * @param orderId the ID of the order
     * @param targetStatus the target status
     * @return true if transition is allowed
     */
    public boolean canOrderTransitionTo(UUID orderId, OrderStatus targetStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        
        return orderStateManager.canTransitionTo(order, targetStatus);
    }

    // ========================================
    // COMMAND PATTERN INTEGRATION METHODS
    // ========================================
    
    /**
     * Creates an order using Command Pattern with undo capability.
     * 
     * @param userId the user ID
     * @param request the order creation request
     * @return CommandResult with order data or error information
     */
    public CommandResult createOrderWithCommand(UUID userId, CreateOrderRequestDTO request) {
        var createCommand = commandFactory.createOrderCommand(userId, request);
        return commandInvoker.execute(createCommand);
    }
    
    /**
     * Cancels an order using Command Pattern.
     * 
     * @param orderId the order ID to cancel
     * @param reason the cancellation reason
     * @return CommandResult with order data or error information
     */
    public CommandResult cancelOrderWithCommand(UUID orderId, String reason) {
        var cancelCommand = commandFactory.cancelOrderCommand(orderId, reason);
        return commandInvoker.execute(cancelCommand);
    }
    
    /**
     * Gets command history information.
     * 
     * @return summary of available undo operations
     */
    public String getCommandHistorySummary() {
        int undoableCommands = commandInvoker.getUndoableCommandCount();
        String lastCommand = commandInvoker.getLastUndoableCommandDescription();
        
        if (undoableCommands == 0) {
            return "No commands available for undo";
        }
        
        return String.format("Commands available for undo: %d. Last command: %s", 
                undoableCommands, lastCommand);
    }
    
    /**
     * Undoes the last command that supports undo operations.
     * 
     * @return CommandResult with undo operation results
     */
    public CommandResult undoLastCommand() {
        return commandInvoker.undoLast();
    }
    
    /**
     * Checks if there are any commands that can be undone.
     * 
     * @return true if there are undoable commands
     */
    public boolean hasUndoableCommands() {
        return commandInvoker.getUndoableCommandCount() > 0;
    }
}
