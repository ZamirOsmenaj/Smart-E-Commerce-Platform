package com.example.ecommerce.observer;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observer that releases inventory when orders are cancelled.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryReleaseObserver implements OrderStatusObserver {
    
    private final InventoryService inventoryService;
    
    @Override
    public void onStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (newStatus == OrderStatus.CANCELLED) {
            log.info("Releasing inventory for cancelled order: {}", order.getId());
            
            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
                log.debug("Released {} units of product {} for order {}", 
                        item.getQuantity(), item.getProductId(), order.getId());
            }
        }
    }
    
    @Override
    public boolean shouldNotify(OrderStatus oldStatus, OrderStatus newStatus) {
        return newStatus == OrderStatus.CANCELLED;
    }
}