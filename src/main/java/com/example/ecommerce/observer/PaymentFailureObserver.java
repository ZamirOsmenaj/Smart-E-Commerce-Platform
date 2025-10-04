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
 * Observer that handles payment failure notifications when orders are cancelled.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentFailureObserver implements OrderStatusObserver {
    
    private final EcommerceNotificationService notificationService;
    private final UserService userService;
    
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            try {
                User customer = userService.findById(order.getUserId());
                log.info("Sending payment failure notification for order {} to user {}", 
                    order.getId(), customer.getEmail());
                notificationService.sendPaymentFailureNotification(order, customer, "Order has been cancelled :(");
            } catch (RuntimeException e) {
                log.warn("Could not send payment failure notification - user not found: {}", order.getUserId());
            }
        }
    }
    
    @Override
    public boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        // Only notify when order is cancelled (payment failure)
        return newStatus == OrderStatus.CANCELLED;
    }
}