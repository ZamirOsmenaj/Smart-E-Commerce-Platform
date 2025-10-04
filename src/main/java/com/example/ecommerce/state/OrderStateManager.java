package com.example.ecommerce.state;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * STATE PATTERN: Manager for order state validation and transitions.
 * 
 * This class provides a clean API for state validation without implementing
 * business logic. It focuses solely on state management concerns:
 * - Validating if operations are allowed in current state
 * - Managing state transitions
 * - Providing state information
 * 
 * Business logic remains in dedicated services (PaymentService, OrderService).
 */
@Component
@Slf4j
public class OrderStateManager {
    
    /**
     * Validates if payment processing is allowed for the given order.
     * 
     * @param order the order to validate
     * @return true if payment can be processed
     */
    public boolean canProcessPayment(Order order) {
        OrderState state = getStateForOrder(order);
        boolean canProcess = state.canProcessPayment();
        
        log.debug("STATE MANAGER: Order {} - Can process payment: {}", order.getId(), canProcess);
        return canProcess;
    }
    
    /**
     * Validates if order cancellation is allowed for the given order.
     * 
     * @param order the order to validate
     * @return true if order can be cancelled
     */
    public boolean canCancelOrder(Order order) {
        OrderState state = getStateForOrder(order);
        boolean canCancel = state.canCancel();
        
        log.debug("STATE MANAGER: Order {} - Can cancel: {}", order.getId(), canCancel);
        return canCancel;
    }
    
    /**
     * Validates if order refund is allowed for the given order.
     * 
     * @param order the order to validate
     * @return true if order can be refunded
     */
    public boolean canRefundOrder(Order order) {
        OrderState state = getStateForOrder(order);
        boolean canRefund = state.canRefund();
        
        log.debug("STATE MANAGER: Order {} - Can refund: {}", order.getId(), canRefund);
        return canRefund;
    }
    
    /**
     * Validates if a state transition is allowed.
     * 
     * @param order the order
     * @param targetStatus the target status
     * @return true if transition is allowed
     */
    public boolean canTransitionTo(Order order, OrderStatus targetStatus) {
        OrderState state = getStateForOrder(order);
        boolean canTransition = state.canTransitionTo(targetStatus);
        
        log.debug("STATE MANAGER: Order {} - Can transition from {} to {}: {}", 
                order.getId(), order.getStatus(), targetStatus, canTransition);
        return canTransition;
    }
    
    /**
     * Gets available actions for the order in its current state.
     * 
     * @param order the order
     * @return description of available actions
     */
    public String getAvailableActions(Order order) {
        OrderState state = getStateForOrder(order);
        return state.getAvailableActions();
    }
    
    /**
     * Gets the reason why an operation is not allowed.
     * 
     * @param order the order
     * @param operation the operation that was attempted
     * @return descriptive error message
     */
    public String getOperationNotAllowedReason(Order order, String operation) {
        OrderState state = getStateForOrder(order);
        return state.getOperationNotAllowedReason(operation);
    }
    
    /**
     * Validates an operation and throws an exception if not allowed.
     * 
     * @param order the order
     * @param operation the operation to validate
     * @throws IllegalStateException if operation is not allowed
     */
    public void validateOperation(Order order, String operation) {
        log.info("STATE MANAGER: Validating operation '{}' for order {} with status {}", 
                operation, order.getId(), order.getStatus());
        
        boolean allowed = switch (operation.toLowerCase()) {
            case "payment", "pay" -> canProcessPayment(order);
            case "cancel" -> canCancelOrder(order);
            case "refund" -> canRefundOrder(order);
            default -> false;
        };
        
        log.info("STATE MANAGER: Operation '{}' allowed for order {}: {}", 
                operation, order.getId(), allowed);
        
        if (!allowed) {
            String reason = getOperationNotAllowedReason(order, operation);
            log.warn("STATE MANAGER: Operation '{}' REJECTED for order {}: {}", 
                    operation, order.getId(), reason);
            throw new IllegalStateException(reason);
        }
        
        log.info("STATE MANAGER: Operation '{}' APPROVED for order {}", operation, order.getId());
    }
    
    /**
     * Validates a state transition and throws an exception if not allowed.
     * 
     * @param order the order
     * @param targetStatus the target status
     * @throws IllegalStateException if transition is not allowed
     */
    public void validateTransition(Order order, OrderStatus targetStatus) {
        if (!canTransitionTo(order, targetStatus)) {
            String message = String.format("Cannot transition order %s from %s to %s", 
                    order.getId(), order.getStatus(), targetStatus);
            log.warn("STATE MANAGER: {}", message);
            throw new IllegalStateException(message);
        }
    }
    
    /**
     * Factory method to get the appropriate state object for an order.
     * 
     * @param order the order
     * @return the corresponding state object
     */
    private OrderState getStateForOrder(Order order) {
        return switch (order.getStatus()) {
            case PENDING -> new PendingOrderState();
            case PAID -> new PaidOrderState();
            case CANCELLED -> new CancelledOrderState();
        };
    }
}