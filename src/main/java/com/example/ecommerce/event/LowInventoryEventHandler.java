package com.example.ecommerce.event;

import com.example.ecommerce.decorator.EcommerceNotificationService;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.proxy.ProductServiceContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Handles low inventory events by sending notifications.
 * This decouples InventoryService from ProductService.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LowInventoryEventHandler {
    
    private final ProductServiceContract productService;
    private final EcommerceNotificationService notificationService;
    
    @EventListener
    public void handleLowInventoryEvent(LowInventoryEvent event) {
        try {
            Product product = productService.findById(event.getProductId());
            log.warn("Low inventory detected for product {} - Current stock: {}", 
                product.getName(), event.getCurrentStock());
            notificationService.sendLowInventoryAlert(product, event.getCurrentStock(), event.getThreshold());
        } catch (RuntimeException e) {
            log.warn("Could not find product {} for low inventory alert", event.getProductId());
        }
    }
}