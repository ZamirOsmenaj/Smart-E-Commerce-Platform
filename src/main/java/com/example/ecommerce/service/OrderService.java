package com.example.ecommerce.service;

import com.example.ecommerce.command.CommandFactory;
import com.example.ecommerce.command.CommandInvoker;
import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.state.OrderStateManager;
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
    private final OrderStatusPublisher orderStatusPublisher;
    
    // Command Pattern components - direct injection now that there's no cyclic dependency
    private final CommandFactory commandFactory;
    private final CommandInvoker commandInvoker;

    /**
     * Creates a new order for a given user based on the provided request.
     * 
     * @deprecated Use createOrderWithCommand() for Command Pattern benefits
     * @param userId  the ID of the user placing the order
     * @param request the request containing order items and quantities
     * @return an {@link OrderResponseDTO} representing the created order
     * @throws RuntimeException if product stock is insufficient
     */
    @Deprecated
    @Transactional
    public OrderResponseDTO createOrder(UUID userId, CreateOrderRequestDTO request) {
        log.warn("Using deprecated createOrder method - consider using createOrderWithCommand() instead");
        
        // Delegate to command-based method
        CommandResult result = createOrderWithCommand(userId, request);
        if (result.isSuccess()) {
            return (OrderResponseDTO) result.getData();
        } else {
            throw new RuntimeException(result.getMessage());
        }
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
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves specific order based on the order ID for that user
     */
    public OrderResponseDTO getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
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
     * @param statusPublisher the publisher to notify observers of status changes
     */
    @Transactional
    public void cancelOrders(List<Order> orders, String reason, 
                           OrderStatusPublisher statusPublisher) {
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
     * Cancels an order with state validation.
     * 
     * @deprecated Use cancelOrderWithCommand() for Command Pattern benefits
     * @param orderId the ID of the order to cancel
     * @param reason the reason for cancellation
     * @return the updated order response
     */
    @Deprecated
    @Transactional
    public OrderResponseDTO cancelOrder(UUID orderId, String reason) {
        log.warn("Using deprecated cancelOrder method - consider using cancelOrderWithCommand() instead");
        
        // Delegate to command-based method
        CommandResult result = cancelOrderWithCommand(orderId, reason);
        if (result.isSuccess()) {
            return (OrderResponseDTO) result.getData();
        } else {
            throw new RuntimeException(result.getMessage());
        }
    }
    
//    /**
//     * Processes a refund for an order with state validation.
//     *
//     * @deprecated Use refundOrderWithCommand() for Command Pattern benefits
//     * @param orderId the ID of the order to refund
//     * @param reason the reason for refund
//     * @return the updated order response
//     */
//    @Deprecated
//    @Transactional
//    public OrderResponseDTO refundOrder(UUID orderId, String reason) {
//        log.warn("Using deprecated refundOrder method - consider using refundOrderWithCommand() instead");
//
//        // Delegate to command-based method
//        CommandResult result = refundOrderWithCommand(orderId, reason);
//        if (result.isSuccess()) {
//            return (OrderResponseDTO) result.getData();
//        } else {
//            throw new RuntimeException(result.getMessage());
//        }
//    }
    
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
     * COMMAND PATTERN: Encapsulates order creation as a command.
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
     * COMMAND PATTERN: Encapsulates order cancellation as a command.
     * 
     * @param orderId the order ID to cancel
     * @param reason the cancellation reason
     * @return CommandResult with order data or error information
     */
    public CommandResult cancelOrderWithCommand(UUID orderId, String reason) {
        var cancelCommand = commandFactory.cancelOrderCommand(orderId, reason);
        return commandInvoker.execute(cancelCommand);
    }
    
//    /**
//     * Processes an order refund using Command Pattern.
//     *
//     * COMMAND PATTERN: Encapsulates order refund as a command.
//     *
//     * @param orderId the order ID to refund
//     * @param reason the refund reason
//     * @return CommandResult with order data or error information
//     */
//    public CommandResult refundOrderWithCommand(UUID orderId, String reason) {
//        var refundCommand = commandFactory.refundOrderCommand(orderId, reason);
//        return commandInvoker.execute(refundCommand);
//    }
    
//    /**
//     * Batch cancels expired orders using Command Pattern.
//     * This method is typically called by scheduled jobs.
//     *
//     * COMMAND PATTERN: Encapsulates batch operations as commands.
//     *
//     * @param hoursOld orders older than this many hours will be cancelled
//     * @return CommandResult with batch operation results
//     */
//    public CommandResult cancelExpiredOrdersWithCommand(int hoursOld) {
//        log.info("ORDER SERVICE: Starting command-based batch cancellation of orders older than {} hours", hoursOld);
//
//        // Find orders that are older than specified hours and still pending
//        Instant cutoffTime = Instant.now().minus(hoursOld, ChronoUnit.HOURS);
//        List<Order> expiredOrders = findUnpaidOrdersOlderThan(cutoffTime);
//
//        if (expiredOrders.isEmpty()) {
//            log.info("ORDER SERVICE: No expired orders found");
//            return CommandResult.success("No expired orders to cancel", 0);
//        }
//
//        log.info("ORDER SERVICE: Found {} expired orders to cancel", expiredOrders.size());
//
//        // Create and execute batch cancellation command
//        String reason = String.format("Automatic cancellation - order expired after %d hours", hoursOld);
//        var batchCommand = commandFactory.batchCancelOrdersCommand(expiredOrders, reason, orderStatusPublisher);
//
//        return commandInvoker.execute(batchCommand);
//    }
    

    
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

    /**
     * Maps an {@link Order} entity to its corresponding {@link OrderResponseDTO} DTO.
     *
     * @param order the order entity to map
     *
     * @return the {@link OrderResponseDTO} representation of the order
     */
    private OrderResponseDTO mapToResponse(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .items(order.getItems().stream().map(i ->
                        OrderResponseDTO.OrderItemResponse.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build()
                ).toList())
                .build();
    }
}
