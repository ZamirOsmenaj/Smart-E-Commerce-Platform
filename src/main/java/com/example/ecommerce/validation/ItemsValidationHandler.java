package com.example.ecommerce.validation;

import com.example.ecommerce.dto.CreateOrderRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates that the order has items and quantities are valid.
 */
@Component
@Slf4j
public class ItemsValidationHandler extends OrderValidationHandler {
    
    @Override
    protected ValidationResult doValidate(CreateOrderRequestDTO request) {
        log.debug("Validating order items");
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ValidationResult.failure(getValidationName(), "Order must contain at least one item");
        }
        
        for (int i = 0; i < request.getItems().size(); i++) {
            var item = request.getItems().get(i);
            
            if (item.getProductId() == null) {
                return ValidationResult.failure(getValidationName(), 
                    String.format("Item %d: Product ID is required", i + 1));
            }
            
            if (item.getQuantity() <= 0) {
                return ValidationResult.failure(getValidationName(), 
                    String.format("Item %d: Quantity must be positive", i + 1));
            }
            
            if (item.getQuantity() > 100) { // Business rule: max 100 per item
                return ValidationResult.failure(getValidationName(), 
                    String.format("Item %d: Quantity cannot exceed 100", i + 1));
            }
        }
        
        log.debug("Items validation passed for {} items", request.getItems().size());
        return ValidationResult.success(getValidationName());
    }
    
    @Override
    protected String getValidationName() {
        return "Items Validation";
    }
}