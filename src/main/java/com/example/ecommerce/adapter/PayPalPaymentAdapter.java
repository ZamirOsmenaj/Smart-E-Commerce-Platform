package com.example.ecommerce.adapter;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.PaymentResponseDTO;
import com.example.ecommerce.external.PayPalPaymentGateway;
import com.example.ecommerce.payment.AbstractPaymentProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * ADAPTER + TEMPLATE METHOD + STRATEGY PATTERN :
 * - Extends AbstractPaymentProcessor to use Template Method pattern
 * - Adapts PayPal's external API to our internal payment flow
 * 
 * This adapter:
 * - Uses Template Method for consistent payment flow (validation, pre/post processing, logging)
 * - Adapts PayPal's specific API in the doProcessPayment() method
 */
@Component("paypalPayment")
@ConditionalOnProperty(name = "app.payment.paypal.enabled", havingValue = "true", matchIfMissing = false)
@RequiredArgsConstructor
@Slf4j
public class PayPalPaymentAdapter extends AbstractPaymentProcessor {
    
    private final PayPalPaymentGateway paypalGateway;
    
    @Override
    protected PaymentResponseDTO doProcessPayment(Order order) {
        log.info("PAYPAL ADAPTER: Processing payment for order {} via PayPal", order.getId());
        
        try {
            // ADAPTER PATTERN: Convert our data to PayPal's expected format
            String payerEmail = "user" + order.getUserId().toString().substring(0, 8) + "@example.com";
            String description = "Order #" + order.getId() + " - " + order.getItems().size() + " items";
            
            PayPalPaymentGateway.PayPalPaymentRequest paypalRequest = 
                new PayPalPaymentGateway.PayPalPaymentRequest(
                    payerEmail, 
                    order.getTotal(), 
                    "USD", 
                    description
                );
            
            // Call PayPal's API using their specific method and parameters
            PayPalPaymentGateway.PayPalTransactionResponse paypalResult = 
                paypalGateway.executePayment(paypalRequest);
            
            // ADAPTER PATTERN: Convert PayPal's response to our standard format
            OrderStatus orderStatus = convertPayPalStateToOrderStatus(paypalResult.state);
            
            log.info("PAYPAL ADAPTER: Payment processed - PayPal State: {}, Our Status: {}", 
                    paypalResult.state, orderStatus);
            
            return new PaymentResponseDTO(order.getId(), orderStatus);
            
        } catch (Exception e) {
            log.error("PAYPAL ADAPTER: Payment processing failed for order {}", order.getId(), e);
            return new PaymentResponseDTO(order.getId(), OrderStatus.CANCELLED);
        }
    }
    
    @Override
    protected String getProviderName() {
        return "PayPal";
    }
    
    @Override
    protected void preProcess(Order order) {
        super.preProcess(order);
        log.debug("PAYPAL ADAPTER: Pre-processing - validating PayPal-specific requirements");
        
        // PayPal-specific pre-processing
        if (order.getTotal().compareTo(BigDecimal.valueOf(1.00)) < 0) {
            throw new IllegalArgumentException("PayPal requires minimum $1.00 payment");
        }
    }
    
    @Override
    protected void postProcess(Order order, PaymentResponseDTO response) {
        super.postProcess(order, response);
        
        if (response.getOrderStatus() == OrderStatus.PAID) {
            log.info("PAYPAL ADAPTER: Payment successful - PayPal transaction completed");
            // Could store PayPal-specific transaction details
        }
    }
    
    /**
     * ADAPTER PATTERN: Converts PayPal's state codes to our internal OrderStatus enum.
     */
    private OrderStatus convertPayPalStateToOrderStatus(String paypalState) {
        return switch (paypalState.toUpperCase()) {
            case "COMPLETED" -> OrderStatus.PAID;
            case "DECLINED", "FAILED", "EXPIRED" -> OrderStatus.CANCELLED;
            case "PENDING", "CREATED" -> OrderStatus.PENDING;
            default -> {
                log.warn("PAYPAL ADAPTER: Unknown PayPal state: {}, defaulting to CANCELLED", paypalState);
                yield OrderStatus.CANCELLED;
            }
        };
    }
}