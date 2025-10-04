package com.example.ecommerce.validation;

import com.example.ecommerce.dto.request.CreateOrderRequestDTO;

/**
 * Chain of Responsibility pattern for order validation.
 * Each handler validates one aspect of the order.
 */
public abstract class OrderValidationHandler {
    
    private OrderValidationHandler nextHandler;
    
    public void setNext(OrderValidationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    
    /**
     * Validate the order and pass to next handler if validation passes.
     */
    public final ValidationResult validate(CreateOrderRequestDTO request) {
        ValidationResult result = doValidate(request);
        
        if (result.isValid() && nextHandler != null) {
            return nextHandler.validate(request);
        }
        
        return result;
    }
    
    /**
     * Perform the specific validation logic for this handler.
     */
    protected abstract ValidationResult doValidate(CreateOrderRequestDTO request);
    
    /**
     * Get the name of this validation step (for logging/debugging).
     */
    protected abstract String getValidationName();
}