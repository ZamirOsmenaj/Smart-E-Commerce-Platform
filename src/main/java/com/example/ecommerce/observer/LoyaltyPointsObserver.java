package com.example.ecommerce.observer;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Observer that manages loyalty points based on order status changes.
 */
@Component
@Slf4j
public class LoyaltyPointsObserver implements OrderStatusObserver {
    
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        
        // Award points when order is paid
        if (oldStatus == OrderStatus.PENDING && newStatus == OrderStatus.PAID) {
            int points = calculateLoyaltyPoints(order.getTotal());
            awardLoyaltyPoints(order.getUserId(), points);
            log.info("Awarded {} loyalty points to user {} for order {}", 
                    points, order.getUserId(), order.getId());
        }
        
        // Remove points if paid order is later cancelled (refund scenario)
        else if (oldStatus == OrderStatus.PAID && newStatus == OrderStatus.CANCELLED) {
            int points = calculateLoyaltyPoints(order.getTotal());
            removeLoyaltyPoints(order.getUserId(), points);
            log.info("Removed {} loyalty points from user {} due to order {} cancellation", 
                    points, order.getUserId(), order.getId());
        }
        
        // No action needed for PENDING → CANCELLED (no points were awarded)
    }
    
    @Override
    public boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        // Only care about transitions involving PAID status
        return (oldStatus == OrderStatus.PENDING && newStatus == OrderStatus.PAID) ||
               (oldStatus == OrderStatus.PAID && newStatus == OrderStatus.CANCELLED);
    }
    
    private int calculateLoyaltyPoints(BigDecimal orderTotal) {
        // 1 point per dollar spent
        return orderTotal.intValue();
    }
    
    private void awardLoyaltyPoints(java.util.UUID userId, int points) {
        // In real implementation: loyaltyService.addPoints(userId, points);
        log.debug("Would award {} points to user {}", points, userId);
    }
    
    private void removeLoyaltyPoints(java.util.UUID userId, int points) {
        // In real implementation: loyaltyService.removePoints(userId, points);
        log.debug("Would remove {} points from user {}", points, userId);
    }
}