package com.example.ecommerce.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Simulates Stripe's payment gateway API.
 * In real life, this would be Stripe's SDK with their specific method names and data structures.
 */
@Component
@Slf4j
public class StripePaymentGateway {
    
    /**
     * Stripe's API method for creating a payment intent.
     * Notice: Different method name and parameters than our internal API.
     */
    public StripePaymentResult createPaymentIntent(String customerId, BigDecimal amountInCents, String currency) {
        log.info("STRIPE API: Creating payment intent for customer {} - Amount: {} cents", customerId, amountInCents);
        
        // Simulate Stripe API call
        try {
            Thread.sleep(200); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% success rate
        boolean success = Math.random() > 0.05;
        
        if (success) {
            String paymentIntentId = "pi_" + System.currentTimeMillis();
            log.info("STRIPE API: Payment intent created successfully - ID: {}", paymentIntentId);
            return new StripePaymentResult(paymentIntentId, "succeeded", amountInCents);
        } else {
            log.warn("STRIPE API: Payment intent failed - insufficient funds");
            return new StripePaymentResult(null, "failed", amountInCents);
        }
    }
    
    /**
     * Stripe's specific result structure
     */
    public static class StripePaymentResult {
        public final String paymentIntentId;
        public final String status; // "succeeded", "failed", "requires_action"
        public final BigDecimal amountCents;
        
        public StripePaymentResult(String paymentIntentId, String status, BigDecimal amountCents) {
            this.paymentIntentId = paymentIntentId;
            this.status = status;
            this.amountCents = amountCents;
        }
    }
}