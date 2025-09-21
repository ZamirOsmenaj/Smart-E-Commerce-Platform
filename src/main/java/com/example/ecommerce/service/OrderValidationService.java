package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.validation.ItemsValidationHandler;
import com.example.ecommerce.validation.ProductExistenceValidationHandler;
import com.example.ecommerce.validation.StockAvailabilityValidationHandler;
import com.example.ecommerce.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that uses Chain of Responsibility pattern to validate order requests.
 * Chains multiple validators together for comprehensive order validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderValidationService {
    
    private final ItemsValidationHandler itemsValidator;
    private final ProductExistenceValidationHandler productExistenceValidator;
    private final StockAvailabilityValidationHandler stockAvailabilityValidator;
    
    /**
     * Validates an order request using the Chain of Responsibility pattern.
     * Each validator in the chain checks a specific aspect of the order.
     * 
     * @param request the order request to validate
     * @return ValidationResult indicating success or failure with details
     */
    public ValidationResult validateOrderRequest(CreateOrderRequestDTO request) {
        log.info("Starting order validation chain for request with {} items", 
                request.getItems() != null ? request.getItems().size() : 0);
        
        // CHAIN OF RESPONSIBILITY: Set up the validation chain
        itemsValidator.setNext(productExistenceValidator);
        productExistenceValidator.setNext(stockAvailabilityValidator);
        // stockAvailabilityValidator has no next (end of chain)
        
        // Execute the validation chain
        ValidationResult result = itemsValidator.validate(request);
        
        if (result.isValid()) {
            log.info("Order validation chain completed successfully");
        } else {
            log.warn("Order validation failed at step '{}': {}", 
                    result.getValidationStep(), result.getErrors());
        }
        
        return result;
    }
    
    /**
     * Quick validation check - returns true if order is valid.
     * 
     * @param request the order request to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidOrder(CreateOrderRequestDTO request) {
        return validateOrderRequest(request).isValid();
    }
}