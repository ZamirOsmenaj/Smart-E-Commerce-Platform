package com.example.ecommerce.state;

import com.example.ecommerce.domain.enums.OrderStatus;

/**
 * STATE PATTERN: Concrete state for CANCELLED orders.
 * 
 * In the CANCELLED state, orders:
 * - Cannot be paid (order is cancelled)
 * - Cannot be cancelled again (already cancelled)
 * - Cannot be refunded (refund would have been processed during cancellation)
 * 
 * This is a terminal state - no further transitions are allowed.
 * This state represents orders that have been cancelled, either before or after payment.
 */
public class CancelledOrderState implements OrderState {
    
    @Override
    public boolean canProcessPayment() {
        return false; // Cannot pay for cancelled orders
    }
    
    @Override
    public boolean canCancel() {
        return false; // Cannot cancel already cancelled orders
    }
    
    @Override
    public boolean canRefund() {
        return false; // Cannot refund cancelled orders (refund processed during cancellation)
    }
    
    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CANCELLED;
    }
    
    @Override
    public String getAvailableActions() {
        return "No actions available - order is cancelled";
    }
    
    @Override
    public boolean canTransitionTo(OrderStatus targetStatus) {
        // CANCELLED is a terminal state - no transitions allowed
        return false;
    }
    
    @Override
    public String getOperationNotAllowedReason(String operation) {
        return switch (operation.toLowerCase()) {
            case "payment", "pay" -> "Cannot process payment - order is cancelled";
            case "cancel" -> "Cannot cancel order - already cancelled";
            case "refund" -> "Cannot refund order - order is cancelled (refund may have been processed during cancellation)";
            default -> "Operation '" + operation + "' is not allowed for cancelled orders";
        };
    }
}