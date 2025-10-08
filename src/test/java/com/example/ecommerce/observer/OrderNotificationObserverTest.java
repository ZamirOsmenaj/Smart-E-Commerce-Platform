package com.example.ecommerce.observer;

import com.example.ecommerce.decorator.EcommerceNotificationService;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.User;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderNotificationObserver.
 * Testing observer pattern implementation, notification logic, and conditional behavior.
 */
@ExtendWith(MockitoExtension.class)
class OrderNotificationObserverTest {

    @Mock
    private EcommerceNotificationService notificationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderNotificationObserver observer;

    private Order testOrder;
    private User testUser;
    private UUID userId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        testUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .build();

        testOrder = Order.builder()
                .id(orderId)
                .userId(userId)
                .total(new BigDecimal("99.99"))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void shouldNotifyWhenOrderStatusChangesToPaid() {
        when(userService.findById(userId)).thenReturn(testUser);
        observer.onStatusChanged(testOrder, OrderStatus.PENDING, OrderStatus.PAID);

        verify(userService).findById(userId);
        verify(notificationService).sendOrderStatusUpdate(
                eq(testOrder), 
                eq(testUser), 
                eq("PAID - Payment Confirmed!")
        );
    }

    @Test
    void shouldNotifyWhenOrderStatusChangesToCancelledFromPending() {
        when(userService.findById(userId)).thenReturn(testUser);
        observer.onStatusChanged(testOrder, OrderStatus.PENDING, OrderStatus.CANCELLED);

        verify(userService).findById(userId);
        verify(notificationService).sendOrderStatusUpdate(
                eq(testOrder), 
                eq(testUser), 
                contains("CANCELLED - due to payment failure or timeout")
        );
    }

    @Test
    void shouldNotifyWhenOrderStatusChangesToCancelledFromPaid() {
        when(userService.findById(userId)).thenReturn(testUser);
        observer.onStatusChanged(testOrder, OrderStatus.PAID, OrderStatus.CANCELLED);

        verify(userService).findById(userId);
        verify(notificationService).sendOrderStatusUpdate(
                eq(testOrder), 
                eq(testUser), 
                contains("CANCELLED - as requested")
        );
    }

    @Test
    void shouldNotNotifyForOtherStatusChanges() {
        when(userService.findById(userId)).thenReturn(testUser);

        // Test a hypothetical status change that shouldn't trigger notifications
        observer.onStatusChanged(testOrder, OrderStatus.PENDING, OrderStatus.PENDING);

        verify(notificationService, never()).sendOrderStatusUpdate(any(), any(), any());
    }

    @Test
    void shouldReturnTrueForImportantStatusChanges() {
        assertTrue(observer.shouldNotify(OrderStatus.PENDING, OrderStatus.PAID));
        assertTrue(observer.shouldNotify(OrderStatus.PENDING, OrderStatus.CANCELLED));
        assertTrue(observer.shouldNotify(OrderStatus.PAID, OrderStatus.CANCELLED));
    }

    @Test
    void shouldImplementOrderStatusObserverInterface() {
        assertInstanceOf(OrderStatusObserver.class, observer);
    }

    @Test
    void shouldHandleMultipleStatusChangesInSequence() {
        when(userService.findById(userId)).thenReturn(testUser);

        // First change: PENDING -> PAID
        observer.onStatusChanged(testOrder, OrderStatus.PENDING, OrderStatus.PAID);
        
        // Second change: PAID -> CANCELLED
        observer.onStatusChanged(testOrder, OrderStatus.PAID, OrderStatus.CANCELLED);

        verify(userService, times(2)).findById(userId);
        verify(notificationService).sendOrderStatusUpdate(
                eq(testOrder), eq(testUser), eq("PAID - Payment Confirmed!"));
        verify(notificationService).sendOrderStatusUpdate(
                eq(testOrder), eq(testUser), contains("CANCELLED - as requested"));
    }

    @Test
    void shouldUseCorrectCancellationReasonBasedOnPreviousStatus() {
        when(userService.findById(userId)).thenReturn(testUser);

        // Test cancellation from PENDING
        observer.onStatusChanged(testOrder, OrderStatus.PENDING, OrderStatus.CANCELLED);
        verify(notificationService).sendOrderStatusUpdate(
                any(), any(), contains("due to payment failure or timeout"));

        reset(notificationService);

        // Test cancellation from PAID
        observer.onStatusChanged(testOrder, OrderStatus.PAID, OrderStatus.CANCELLED);
        verify(notificationService).sendOrderStatusUpdate(
                any(), any(), contains("as requested"));
    }

    @Test
    void shouldWorkWithDifferentOrders() {
        UUID anotherUserId = UUID.randomUUID();
        User anotherUser = User.builder()
                .id(anotherUserId).email("another@example.com")
                .build();

        Order anotherOrder = Order.builder()
                .id(UUID.randomUUID())
                .userId(anotherUserId)
                .total(new BigDecimal("149.99"))
                .status(OrderStatus.PENDING)
                .build();

        when(userService.findById(anotherUserId)).thenReturn(anotherUser);

        observer.onStatusChanged(anotherOrder, OrderStatus.PENDING, OrderStatus.PAID);

        verify(userService).findById(anotherUserId);
        verify(notificationService).sendOrderStatusUpdate(
                eq(anotherOrder), eq(anotherUser), eq("PAID - Payment Confirmed!"));
    }
}