package com.example.ecommerce.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for OrderStatus enum.
 * These are the most basic tests - testing enum values and behavior.
 */
class OrderStatusTest {

    @Test
    void shouldHaveThreeOrderStatuses() {
        OrderStatus[] statuses = OrderStatus.values();
        assertEquals(3, statuses.length);
    }

    @Test
    void shouldContainExpectedStatuses() {
        assertTrue(containsStatus(OrderStatus.PENDING));
        assertTrue(containsStatus(OrderStatus.PAID));
        assertTrue(containsStatus(OrderStatus.CANCELLED));
    }

    @Test
    void shouldConvertFromString() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        assertThrows(IllegalArgumentException.class, () -> 
            OrderStatus.valueOf("INVALID_STATUS"));
    }

    @Test
    void shouldHaveCorrectToString() {
        assertEquals("PENDING", OrderStatus.PENDING.toString());
        assertEquals("PAID", OrderStatus.PAID.toString());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.toString());
    }

    private boolean containsStatus(OrderStatus status) {
        for (OrderStatus s : OrderStatus.values()) {
            if (s == status) {
                return true;
            }
        }
        return false;
    }
}