package com.example.ecommerce.payment;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Abstract base class implementing the Template Method pattern for payment processing.
 * Defines the common payment flow while allowing subclasses to customize specific steps.
 */
@Slf4j
public abstract class AbstractPaymentProcessor implements PaymentStrategy {
    
    /**
     * Template method defining the payment processing flow.
     * This method cannot be overridden by subclasses.
     */
    @Override
    public final PaymentResponseDTO processPayment(Order order) {
        log.info("Starting payment processing for order: {} using provider: {}", 
                order.getId(), getProviderName());
        
        // Step 1: Validate the order
        validateOrder(order);
        
        // Step 2: Pre-process (can be overridden)
        preProcess(order);
        
        // Step 3: Process payment (must be implemented by subclasses)
        PaymentResponseDTO response = doProcessPayment(order);
        
        // Step 4: Post-process (can be overridden)
        postProcess(order, response);
        
        // Step 5: Log the transaction
        logTransaction(order, response);
        
        log.info("Payment processing completed for order: {} with status: {}", 
                order.getId(), response.getOrderStatus());
        
        return response;
    }
    
    /**
     * Validates the order before processing payment.
     * Common validation logic that applies to all payment providers.
     */
    protected void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        if (order.getTotal() == null || order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order total must be greater than zero");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order must be in PENDING status to process payment");
        }
        
        log.debug("Order validation passed for order: {}", order.getId());
    }
    
    /**
     * Pre-processing hook that can be overridden by subclasses.
     * Called before the actual payment processing.
     */
    protected void preProcess(Order order) {
        log.debug("Pre-processing order: {}", order.getId());
        // Default implementation does nothing
    }
    
    /**
     * Abstract method that must be implemented by concrete payment processors.
     * Contains the actual payment processing logic specific to each provider.
     */
    protected abstract PaymentResponseDTO doProcessPayment(Order order);
    
    /**
     * Post-processing hook that can be overridden by subclasses.
     * Called after the payment processing is complete.
     */
    protected void postProcess(Order order, PaymentResponseDTO response) {
        log.debug("Post-processing order: {} with result: {}", order.getId(), response.getOrderStatus());
        // Default implementation does nothing
    }
    
    /**
     * Logs the payment transaction.
     * Common logging logic for all payment providers.
     */
    protected void logTransaction(Order order, PaymentResponseDTO response) {
        log.info("Payment transaction logged - Order: {}, Provider: {}, Status: {}, Amount: {}", 
                order.getId(), getProviderName(), response.getOrderStatus(), order.getTotal());
        
        // In a real implementation, this might write to an audit table
    }
    
    /**
     * Returns the name of the payment provider.
     * Used for logging and identification purposes.
     */
    protected abstract String getProviderName();
}