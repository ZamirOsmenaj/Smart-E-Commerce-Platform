package com.example.ecommerce.observer;

import com.example.ecommerce.decorator.EcommerceNotificationService;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observer that handles notifications when order status changes.
 * Now uses the Decorator pattern notification system for flexible multichannel notifications.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationObserver implements OrderStatusObserver {
    
    private final EcommerceNotificationService notificationService;
    private final UserService userService;
    
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        User customer = userService.findById(order.getUserId())
                .orElse(null);

        if (customer == null) {
            log.warn("Customer not found for order {}, skipping notification", order.getId());
            return;
        }

        switch (newStatus) {
            case PAID -> {
                log.info("Sending payment confirmation to user {} for order {}", 
                    customer.getId(), order.getId());
                notificationService.sendOrderStatusUpdate(order, customer, "PAID - Payment Confirmed!");
            }
            case CANCELLED -> {
                String reason = oldStatus == OrderStatus.PENDING ? 
                    "due to payment failure or timeout" : "as requested";
                log.info("Sending cancellation notice to user {} for order {} (reason: {})", 
                    customer.getId(), order.getId(), reason);
                notificationService.sendOrderStatusUpdate(order, customer, "CANCELLED - " + reason);
            }
        }
    }
    
    @Override
    public boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        // Only notify for important status changes
        return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
    }
}