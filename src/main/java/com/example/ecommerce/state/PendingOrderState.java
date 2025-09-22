package com.example.ecommerce.state;

import com.example.ecommerce.domain.enums.OrderStatus;

/**
 * STATE PATTERN: Concrete state for PENDING orders.
 * 
 * In the PENDING state, orders:
 * - Can be paid (transition to PAID)
 * - Can be cancelled (transition to CANCELLED)
 * - Cannot be refunded (no payment has been made)
 * 
 * This state focuses on validation and transition rules only.
 * Business logic is handled by PaymentService, OrderService, etc.
 */
public class PendingOrderState implements OrderState {
    
    @Override
    public boolean canProcessPayment() {
        return true; // Pending orders can be paid
    }
    
    @Override
    public boolean canCancel() {
        return true; // Pending orders can be cancelled
    }
    
    @Override
    public boolean canRefund() {
        return false; // Cannot refund unpaid orders
    }
    
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PENDING;
    }
    
    @Override
    public String getAvailableActions() {
        return "Available actions: Process Payment, Cancel Order";
    }
    
    @Override
    public boolean canTransitionTo(OrderStatus targetStatus) {
        return targetStatus == OrderStatus.PAID || targetStatus == OrderStatus.CANCELLED;
    }
    
    @Override
    public String getOperationNotAllowedReason(String operation) {
        return switch (operation.toLowerCase()) {
            case "refund" -> "Cannot refund order - no payment has been made yet";
            default -> "Operation '" + operation + "' is not allowed for pending orders";
        };
    }
}