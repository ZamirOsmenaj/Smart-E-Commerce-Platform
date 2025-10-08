package com.example.ecommerce.state;

import com.example.ecommerce.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PendingOrderState.
 * Testing state pattern implementation, validation rules, and transitions.
 */
class PendingOrderStateTest {

    private PendingOrderState pendingState;

    @BeforeEach
    void setUp() {
        pendingState = new PendingOrderState();
    }

    @Test
    void shouldAllowPaymentProcessing() {
        assertTrue(pendingState.canProcessPayment());
    }

    @Test
    void shouldAllowCancellation() {
        assertTrue(pendingState.canCancel());
    }

    @Test
    void shouldNotAllowRefund() {
        assertFalse(pendingState.canRefund());
    }

    @Test
    void shouldReturnCorrectStatus() {
        assertEquals(OrderStatus.PENDING, pendingState.getStatus());
    }

    @Test
    void shouldProvideAvailableActions() {
        String actions = pendingState.getAvailableActions();
        
        assertNotNull(actions);
        assertTrue(actions.contains("Process Payment"));
        assertTrue(actions.contains("Cancel Order"));
    }

    @Test
    void shouldAllowTransitionToPaid() {
        assertTrue(pendingState.canTransitionTo(OrderStatus.PAID));
    }

    @Test
    void shouldAllowTransitionToCancelled() {
        assertTrue(pendingState.canTransitionTo(OrderStatus.CANCELLED));
    }

    @Test
    void shouldNotAllowTransitionToPending() {
        assertFalse(pendingState.canTransitionTo(OrderStatus.PENDING));
    }

    @Test
    void shouldProvideRefundNotAllowedReason() {
        String reason = pendingState.getOperationNotAllowedReason("refund");
        
        assertNotNull(reason);
        assertTrue(reason.contains("Cannot refund order"));
        assertTrue(reason.contains("no payment has been made"));
    }

    @Test
    void shouldProvideGenericNotAllowedReason() {
        String reason = pendingState.getOperationNotAllowedReason("unknown");
        
        assertNotNull(reason);
        assertTrue(reason.contains("Operation 'unknown' is not allowed"));
        assertTrue(reason.contains("pending orders"));
    }

    @Test
    void shouldHandleCaseInsensitiveOperations() {
        String reasonLower = pendingState.getOperationNotAllowedReason("refund");
        String reasonUpper = pendingState.getOperationNotAllowedReason("REFUND");
        String reasonMixed = pendingState.getOperationNotAllowedReason("Refund");
        
        // All should return the specific refund message
        assertTrue(reasonLower.contains("Cannot refund order"));
        assertTrue(reasonUpper.contains("Cannot refund order"));
        assertTrue(reasonMixed.contains("Cannot refund order"));
    }

    @Test
    void shouldBeConsistentWithStateValidation() {
        // If canProcessPayment returns true, transition to PAID should be allowed
        if (pendingState.canProcessPayment()) {
            assertTrue(pendingState.canTransitionTo(OrderStatus.PAID));
        }
        
        // If canCancel returns true, transition to CANCELLED should be allowed
        if (pendingState.canCancel()) {
            assertTrue(pendingState.canTransitionTo(OrderStatus.CANCELLED));
        }
        
        // If canRefund returns false, refund operation should have a reason
        if (!pendingState.canRefund()) {
            String reason = pendingState.getOperationNotAllowedReason("refund");
            assertNotNull(reason);
            assertFalse(reason.isEmpty());
        }
    }
}