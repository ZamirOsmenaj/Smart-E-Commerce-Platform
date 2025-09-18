package com.example.ecommerce.observer;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observer that handles notifications when order status changes.
 * In a real implementation, this would send emails, SMS, or push notifications.
 */
@Component
@Slf4j
public class OrderNotificationObserver implements OrderStatusObserver {
    
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        switch (newStatus) {
            case PAID -> sendPaymentConfirmation(order, oldStatus);
            case CANCELLED -> sendCancellationNotice(order, oldStatus);
            default -> log.debug("No notification needed for status change to {}", newStatus);
        }
    }
    
    @Override
    public boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
    }
    
    private void sendPaymentConfirmation(Order order, OrderStatus oldStatus) {
        String message = String.format(
            "Payment confirmed for order %s. Status changed from %s to PAID.", 
            order.getId(), oldStatus
        );
        
        log.info("Sending payment confirmation to user {}: {}", order.getUserId(), message);
        // TODO: Implement actual notification logic (email, SMS, etc.)
        // emailService.sendPaymentConfirmation(order.getUserId(), message);
    }
    
    private void sendCancellationNotice(Order order, OrderStatus oldStatus) {
        String reason = oldStatus == OrderStatus.PENDING ? 
            "due to payment failure or timeout" : 
            "as a refund";
            
        String message = String.format(
            "Order %s has been cancelled %s. Previous status was %s.", 
            order.getId(), reason, oldStatus
        );
        
        log.info("Sending cancellation notice to user {}: {}", order.getUserId(), message);
        // TODO: Implement actual notification logic (email, SMS, etc.)
        // emailService.sendCancellationNotice(order.getUserId(), message);
    }
}