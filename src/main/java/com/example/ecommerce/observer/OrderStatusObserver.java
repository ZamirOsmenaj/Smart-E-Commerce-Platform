package com.example.ecommerce.observer;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;

/**
 * Observer interface for order status changes.
 * Implementations can react to order status transitions.
 */
public interface OrderStatusObserver {
    
    /**
     * Called when an order's status changes.
     *
     * @param order the order that changed
     * @param oldStatus the previous status
     * @param newStatus the new status
     */
    void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
    
    /**
     * Determines if this observer should be notified for the given status change.
     *
     * @param oldStatus the previous status
     * @param newStatus the new status
     * @return true if this observer should be notified
     */
    default boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        return true;
    }
}