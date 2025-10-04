package com.example.ecommerce.observer;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Publisher that notifies observers when order status changes occur.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusPublisher {
    
    private final List<OrderStatusObserver> observers;
    
    /**
     * Notifies all registered observers of an order status change.
     *
     * @param order the order that changed
     * @param oldStatus the previous status
     * @param newStatus the new status
     */
    public void notifyStatusChange(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        log.info("Publishing order status change: Order {} from {} to {}", 
                order.getId(), oldStatus, newStatus);
        
        observers.stream()
                .filter(observer -> observer.shouldNotify(oldStatus, newStatus))
                .forEach(observer -> {
                    try {
                        observer.onStatusChanged(order, oldStatus, newStatus);
                    } catch (Exception e) {
                        log.error("Error notifying observer {} for order {}", 
                                observer.getClass().getSimpleName(), order.getId(), e);
                    }
                });
    }
}