package com.example.ecommerce.validation;

import com.example.ecommerce.dto.request.CreateOrderRequestDTO;
import com.example.ecommerce.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates that sufficient stock is available for all items in the order.
 * This prevents orders that cannot be fulfilled due to insufficient inventory.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StockAvailabilityValidationHandler extends OrderValidationHandler {
    
    private final InventoryService inventoryService;
    
    @Override
    protected ValidationResult doValidate(CreateOrderRequestDTO request) {
        log.debug("Validating stock availability for {} items", request.getItems().size());
        
        for (int i = 0; i < request.getItems().size(); i++) {
            var item = request.getItems().get(i);
            
            try {
                var inventory = inventoryService.findById(item.getProductId());
                
                if (inventory.getAvailable() < item.getQuantity()) {
                    return ValidationResult.failure(getValidationName(), 
                        String.format("Item %d: Insufficient stock. Requested: %d, Available: %d", 
                                i + 1, item.getQuantity(), inventory.getAvailable()));
                }
                
                log.debug("Stock check passed for product {}: requested={}, available={}", 
                        item.getProductId(), item.getQuantity(), inventory.getAvailable());
                
            } catch (RuntimeException e) {
                return ValidationResult.failure(getValidationName(), 
                    String.format("Item %d: No inventory found for product %s", 
                            i + 1, item.getProductId()));
            }
        }
        
        log.debug("Stock availability validation passed for all {} items", request.getItems().size());
        return ValidationResult.success(getValidationName());
    }
    
    @Override
    protected String getValidationName() {
        return "Stock Availability Validation";
    }
}