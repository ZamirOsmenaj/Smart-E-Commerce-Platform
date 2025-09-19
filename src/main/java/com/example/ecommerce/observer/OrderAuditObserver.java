package com.example.ecommerce.observer;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Observer that creates audit logs for order status transitions.
 */
@Component
@Slf4j
public class OrderAuditObserver implements OrderStatusObserver {
    
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        // Create audit log entry
        String auditMessage = String.format(
            "Order %s status changed from %s to %s at %s", 
            order.getId(), 
            oldStatus, 
            newStatus, 
            Instant.now()
        );
        
        log.info("AUDIT: {}", auditMessage);
        
        // In a real system, you'd save this to an audit table:
        // auditService.logStatusChange(order.getId(), oldStatus, newStatus, Instant.now());
        
        // Special handling for specific transitions
        if (oldStatus == OrderStatus.PENDING && newStatus == OrderStatus.PAID) {
            log.info("AUDIT: Order {} completed payment successfully", order.getId());
        } else if (oldStatus == OrderStatus.PENDING && newStatus == OrderStatus.CANCELLED) {
            log.warn("AUDIT: Order {} was cancelled before payment", order.getId());
        }
    }
    
    @Override
    public boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        // We want to audit ALL status changes
        return !oldStatus.equals(newStatus); // Only if status actually changed
    }
}