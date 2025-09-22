package com.example.ecommerce.state;

import com.example.ecommerce.domain.enums.OrderStatus;

/**
 * STATE PATTERN: Concrete state for PAID orders.
 * 
 * In the PAID state, orders:
 * - Cannot be paid again (already paid)
 * - Can be refunded (transition to CANCELLED with refund)
 * - Can be cancelled with refund processing
 * 
 * This state focuses on validation and transition rules only.
 * Business logic is handled by PaymentService, OrderService, etc.
 */
public class PaidOrderState implements OrderState {
    
    @Override
    public boolean canProcessPayment() {
        return false; // Cannot pay for already paid orders
    }
    
    @Override
    public boolean canCancel() {
        return true; // Paid orders can be cancelled (with refund)
    }
    
    @Override
    public boolean canRefund() {
        return true; // Paid orders can be refunded
    }
    
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PAID;
    }
    
    @Override
    public String getAvailableActions() {
        return "Available actions: Process Refund, Cancel Order (with refund)";
    }
    
    @Override
    public boolean canTransitionTo(OrderStatus targetStatus) {
        // Paid orders can only transition to CANCELLED (via refund or cancellation)
        return targetStatus == OrderStatus.CANCELLED;
    }
    
    @Override
    public String getOperationNotAllowedReason(String operation) {
        return switch (operation.toLowerCase()) {
            case "payment", "pay" -> "Cannot process payment - order is already paid";
            default -> "Operation '" + operation + "' is not allowed for paid orders";
        };
    }
}