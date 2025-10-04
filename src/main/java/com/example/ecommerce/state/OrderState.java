package com.example.ecommerce.state;

import com.example.ecommerce.enums.OrderStatus;

/**
 * STATE PATTERN: Abstract state interface for Order lifecycle management.
 * 
 * This interface defines the contract for all order states, focusing on
 * state validation and transition rules rather than business logic implementation.
 * 
 * Key responsibilities:
 * - Validate if operations are allowed in current state
 * - Define valid state transitions
 * - Provide state-specific information
 * 
 * Business logic (payment processing, cancellation, etc.) remains in 
 * dedicated services (PaymentService, OrderService).
 */
public interface OrderState {
    
    /**
     * Validates if payment processing is allowed in this state.
     * 
     * @return true if payment can be processed, false otherwise
     */
    boolean canProcessPayment();
    
    /**
     * Validates if order cancellation is allowed in this state.
     * 
     * @return true if order can be cancelled, false otherwise
     */
    boolean canCancel();
    
    /**
     * Validates if order refund is allowed in this state.
     * 
     * @return true if order can be refunded, false otherwise
     */
    boolean canRefund();
    
    /**
     * Gets the current status represented by this state.
     * 
     * @return the OrderStatus enum value
     */
    OrderStatus getStatus();
    
    /**
     * Gets a human-readable description of what actions are available in this state.
     * 
     * @return description of available actions
     */
    String getAvailableActions();
    
    /**
     * Validates if a specific transition is allowed from this state.
     * 
     * @param targetStatus the target status to transition to
     * @return true if transition is allowed, false otherwise
     */
    boolean canTransitionTo(OrderStatus targetStatus);
    
    /**
     * Gets the reason why an operation is not allowed (for error messages).
     * 
     * @param operation the operation that was attempted
     * @return descriptive error message
     */
    String getOperationNotAllowedReason(String operation);
}