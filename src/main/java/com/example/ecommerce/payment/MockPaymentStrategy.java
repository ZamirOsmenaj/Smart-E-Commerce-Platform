package com.example.ecommerce.payment;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Mock implementation of a payment provider, using Strategy and Template Method patterns.
 */
@Component("mockPayment")
@Slf4j
public class MockPaymentStrategy extends AbstractPaymentProcessor {

    @Override
    protected PaymentResponseDTO doProcessPayment(Order order) {
        log.info("Processing mock payment for order: {} with amount: {}", 
                order.getId(), order.getTotal());
        
        // Simulate payment processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Always succeed for now (90% success rate in real scenario)
        boolean success = Math.random() > 0.1;
        OrderStatus status = success ? OrderStatus.PAID : OrderStatus.CANCELLED;
        
        log.info("Mock payment {} for order: {}", 
                success ? "succeeded" : "failed", order.getId());
        
        return new PaymentResponseDTO(order.getId(), status);
    }
    
    @Override
    protected String getProviderName() {
        return "MockPayment";
    }
    
    @Override
    protected void preProcess(Order order) {
        super.preProcess(order);
        log.debug("Mock payment pre-processing: validating mock conditions for order {}", 
                order.getId());
    }
}
