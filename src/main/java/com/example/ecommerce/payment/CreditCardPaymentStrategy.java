package com.example.ecommerce.payment;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Credit card payment implementation using Strategy and Template Method patterns.
 */
@Component("creditCard")
@Slf4j
public class CreditCardPaymentStrategy extends AbstractPaymentProcessor {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000.00");

    @Override
    protected PaymentResponseDTO doProcessPayment(Order order) {
        log.info("Processing credit card payment for order: {} with amount: {}", 
                order.getId(), order.getTotal());
        
        // Simulate credit card processing
        boolean success = processCreditCardTransaction();
        OrderStatus status = success ? OrderStatus.PAID : OrderStatus.CANCELLED;
        
        return new PaymentResponseDTO(order.getId(), status);
    }
    
    @Override
    protected void preProcess(Order order) {
        super.preProcess(order);
        
        // Additional validation for credit card payments
        if (order.getTotal().compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException(
                "Credit card payment amount exceeds maximum limit: " + MAX_AMOUNT);
        }
        
        log.debug("Credit card pre-processing completed for order: {}", order.getId());
    }
    
    @Override
    protected void postProcess(Order order, PaymentResponseDTO response) {
        super.postProcess(order, response);
        
        if (response.getOrderStatus() == OrderStatus.PAID) {
            // Send receipt, update loyalty points, etc.
            log.info("Credit card payment successful - sending receipt for order: {}", 
                    order.getId());
        }
    }
    
    @Override
    protected String getProviderName() {
        return "CreditCard";
    }
    
    private boolean processCreditCardTransaction() {
        // Simulate credit card processing logic
        try {
            Thread.sleep(200); // Simulate network call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // 95% success rate for credit cards
        return Math.random() > 0.05;
    }
}