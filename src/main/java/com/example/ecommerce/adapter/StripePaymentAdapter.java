package com.example.ecommerce.adapter;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import com.example.ecommerce.external.StripePaymentGateway;
import com.example.ecommerce.payment.AbstractPaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * ADAPTER + TEMPLATE METHOD + STRATEGY PATTERN:
 * - Extends AbstractPaymentProcessor to use Template Method pattern
 * - Adapts Stripe's external API to our internal payment flow
 * 
 * This adapter:
 * - Uses Template Method for consistent payment flow (validation, pre/post processing, logging)
 * - Adapts Stripe's specific API in the doProcessPayment() method
 */
@Component("stripePayment")
@ConditionalOnProperty(name = "app.payment.stripe.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class StripePaymentAdapter extends AbstractPaymentProcessor {
    
    private final StripePaymentGateway stripeGateway;
    
    @Override
    protected PaymentResponseDTO doProcessPayment(Order order) {
        log.info("STRIPE ADAPTER: Processing payment for order {} via Stripe", order.getId());
        
        try {
            // ADAPTER PATTERN: Convert our data to Stripe's expected format
            String customerId = "cust_" + order.getUserId().toString().substring(0, 8);
            BigDecimal amountInCents = order.getTotal().multiply(BigDecimal.valueOf(100)); // Stripe uses cents
            String currency = "usd";
            
            // Call Stripe's API using their specific method and parameters
            StripePaymentGateway.StripePaymentResult stripeResult =
                stripeGateway.createPaymentIntent(customerId, amountInCents, currency);
            
            // ADAPTER PATTERN: Convert Stripe's response to our standard format
            OrderStatus orderStatus = convertStripeStatusToOrderStatus(stripeResult.status);
            
            log.info("STRIPE ADAPTER: Payment processed - Stripe Status: {}, Our Status: {}", 
                    stripeResult.status, orderStatus);
            
            return new PaymentResponseDTO(order.getId(), orderStatus);
            
        } catch (Exception e) {
            log.error("STRIPE ADAPTER: Payment processing failed for order {}", order.getId(), e);
            return new PaymentResponseDTO(order.getId(), OrderStatus.CANCELLED);
        }
    }
    
    @Override
    protected String getProviderName() {
        return "Stripe";
    }
    
    @Override
    protected void preProcess(Order order) {
        super.preProcess(order);
        log.debug("STRIPE ADAPTER: Pre-processing - validating Stripe-specific requirements");
        
        // Stripe-specific pre-processing
        if (order.getTotal().compareTo(BigDecimal.valueOf(0.50)) < 0) {
            throw new IllegalArgumentException("Stripe requires minimum $0.50 payment");
        }
    }
    
    @Override
    protected void postProcess(Order order, PaymentResponseDTO response) {
        super.postProcess(order, response);
        
        if (response.getOrderStatus() == OrderStatus.PAID) {
            log.info("STRIPE ADAPTER: Payment successful - Stripe transaction completed");
            // Could store Stripe-specific transaction details
        }
    }
    
    /**
     * ADAPTER PATTERN: Converts Stripe's status codes to our internal OrderStatus enum.
     */
    private OrderStatus convertStripeStatusToOrderStatus(String stripeStatus) {
        return switch (stripeStatus.toLowerCase()) {
            case "succeeded" -> OrderStatus.PAID;
            case "failed", "canceled" -> OrderStatus.CANCELLED;
            case "requires_action", "processing" -> OrderStatus.PENDING;
            default -> {
                log.warn("STRIPE ADAPTER: Unknown Stripe status: {}, defaulting to CANCELLED", stripeStatus);
                yield OrderStatus.CANCELLED;
            }
        };
    }
}