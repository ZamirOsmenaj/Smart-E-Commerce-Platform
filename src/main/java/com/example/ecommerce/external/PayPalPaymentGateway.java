package com.example.ecommerce.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Simulates PayPal's payment gateway API.
 * Notice: Completely different API structure than Stripe - this is why we need adapters!
 */
@Component
@Slf4j
public class PayPalPaymentGateway {
    
    /**
     * PayPal's API method for processing payments.
     * Notice: Different method name, parameters, and response structure than Stripe.
     */
    public PayPalTransactionResponse executePayment(PayPalPaymentRequest request) {
        log.info("PAYPAL API: Executing payment for {} - Amount: ${}", request.payerEmail, request.totalAmount);
        
        // Simulate PayPal API call
        try {
            Thread.sleep(300); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 90% success rate (PayPal is slightly less reliable in our simulation)
        boolean success = Math.random() > 0.1;
        
        if (success) {
            String transactionId = "TXN" + System.currentTimeMillis();
            log.info("PAYPAL API: Payment executed successfully - Transaction ID: {}", transactionId);
            return new PayPalTransactionResponse(transactionId, "COMPLETED", request.totalAmount, "Payment successful");
        } else {
            log.warn("PAYPAL API: Payment execution failed - declined by bank");
            return new PayPalTransactionResponse(null, "DECLINED", request.totalAmount, "Payment declined by issuing bank");
        }
    }
    
    /**
     * PayPal's specific request structure
     */
    public static class PayPalPaymentRequest {
        public final String payerEmail;
        public final BigDecimal totalAmount;
        public final String currency;
        public final String description;
        
        public PayPalPaymentRequest(String payerEmail, BigDecimal totalAmount, String currency, String description) {
            this.payerEmail = payerEmail;
            this.totalAmount = totalAmount;
            this.currency = currency;
            this.description = description;
        }
    }
    
    /**
     * PayPal's specific response structure
     */
    public static class PayPalTransactionResponse {
        public final String transactionId;
        public final String state; // "COMPLETED", "DECLINED", "PENDING"
        public final BigDecimal amount;
        public final String message;
        
        public PayPalTransactionResponse(String transactionId, String state, BigDecimal amount, String message) {
            this.transactionId = transactionId;
            this.state = state;
            this.amount = amount;
            this.message = message;
        }
    }
}