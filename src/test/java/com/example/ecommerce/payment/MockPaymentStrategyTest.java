package com.example.ecommerce.payment;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.response.PaymentResponseDTO;
import com.example.ecommerce.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MockPaymentStrategy.
 * Testing strategy pattern implementation, payment processing, and template method pattern.
 */
class MockPaymentStrategyTest {

    private MockPaymentStrategy paymentStrategy;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        paymentStrategy = new MockPaymentStrategy();
        
        testOrder = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .total(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getOrderId());
        // Status should be either PAID or CANCELLED (due to random success)
        assertTrue(response.getOrderStatus() == OrderStatus.PAID || 
                  response.getOrderStatus() == OrderStatus.CANCELLED);
    }

    @Test
    void shouldReturnCorrectOrderId() {
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        assertEquals(testOrder.getId(), response.getOrderId());
    }

    @Test
    void shouldNotHandleZeroAmount() {
        testOrder.setTotal(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () -> paymentStrategy.processPayment(testOrder));

    }

    @Test
    void shouldHandleLargeAmount() {
        testOrder.setTotal(new BigDecimal("999999.99"));
        
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getOrderId());
    }

    @Test
    void shouldHandleNullOrderGracefully() {
        // This test depends on the AbstractPaymentProcessor implementation
        // If it doesn't handle null, it should throw an appropriate exception
        assertThrows(Exception.class, () -> {
            paymentStrategy.processPayment(null);
        });
    }

    @RepeatedTest(10)
    void shouldHaveConsistentBehaviorAcrossMultipleCalls() {
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getOrderId());
        assertNotNull(response.getOrderStatus());
        
        // Status should always be either PAID or CANCELLED
        assertTrue(response.getOrderStatus() == OrderStatus.PAID || 
                  response.getOrderStatus() == OrderStatus.CANCELLED);
    }

    @Test
    void shouldProcessDifferentOrders() {
        Order order1 = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .total(new BigDecimal("50.00"))
                .status(OrderStatus.PENDING)
                .build();

        Order order2 = Order.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .total(new BigDecimal("150.00"))
                .status(OrderStatus.PENDING)
                .build();

        PaymentResponseDTO response1 = paymentStrategy.processPayment(order1);
        PaymentResponseDTO response2 = paymentStrategy.processPayment(order2);

        assertNotEquals(response1.getOrderId(), response2.getOrderId());
        assertEquals(order1.getId(), response1.getOrderId());
        assertEquals(order2.getId(), response2.getOrderId());
    }

    @Test
    void shouldImplementPaymentStrategyInterface() {
        assertTrue(paymentStrategy instanceof PaymentStrategy);
    }

    @Test
    void shouldExtendAbstractPaymentProcessor() {
        assertTrue(paymentStrategy instanceof AbstractPaymentProcessor);
    }

    @Test
    void shouldHandleHighPrecisionAmounts() {
        testOrder.setTotal(new BigDecimal("99.999999"));
        
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        assertNotNull(response);
        assertEquals(testOrder.getId(), response.getOrderId());
    }

    @Test
    void shouldProcessPaymentWithinReasonableTime() {
        long startTime = System.currentTimeMillis();
        
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within 1 second (includes the 100ms sleep)
        assertTrue(duration < 1000, "Payment processing took too long: " + duration + "ms");
        assertNotNull(response);
    }

    @Test
    void shouldMaintainOrderStatusConsistency() {
        PaymentResponseDTO response = paymentStrategy.processPayment(testOrder);
        
        // If payment succeeded, status should be PAID
        // If payment failed, status should be CANCELLED
        if (response.getOrderStatus() == OrderStatus.PAID) {
            // Payment was successful
            assertTrue(true); // Success case
        } else if (response.getOrderStatus() == OrderStatus.CANCELLED) {
            // Payment failed
            assertTrue(true); // Failure case
        } else {
            fail("Unexpected order status: " + response.getOrderStatus());
        }
    }
}