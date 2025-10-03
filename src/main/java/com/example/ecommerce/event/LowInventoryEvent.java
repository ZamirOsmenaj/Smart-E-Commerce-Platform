package com.example.ecommerce.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

/**
 * Event published when inventory falls below threshold.
 */
@Getter
public class LowInventoryEvent extends ApplicationEvent {
    
    private final UUID productId;
    private final int currentStock;
    private final int threshold;
    
    public LowInventoryEvent(Object source, UUID productId, int currentStock, int threshold) {
        super(source);
        this.productId = productId;
        this.currentStock = currentStock;
        this.threshold = threshold;
    }
}