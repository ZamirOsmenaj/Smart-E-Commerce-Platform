package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for OrderMapper.
 * Testing utility class behavior, mapping logic, and edge cases.
 */
class OrderMapperTest {

    @Test
    void shouldMapOrderToResponseDTO() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Instant createdAt = Instant.now();
        BigDecimal total = new BigDecimal("199.98");

        OrderItem item1 = OrderItem.builder()
                .productId(productId1)
                .quantity(2)
                .price(new BigDecimal("49.99"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productId(productId2)
                .quantity(1)
                .price(new BigDecimal("99.99"))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PAID)
                .total(total)
                .createdAt(createdAt)
                .items(Arrays.asList(item1, item2))
                .build();

        OrderResponseDTO response = MapperFacade.toResponseDTO(order);

        assertNotNull(response);
        assertEquals(orderId, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(OrderStatus.PAID, response.getStatus());
        assertEquals(total, response.getTotal());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(2, response.getItems().size());

        // Check first item
        OrderResponseDTO.OrderItemResponse responseItem1 = response.getItems().get(0);
        assertEquals(productId1, responseItem1.getProductId());
        assertEquals(2, responseItem1.getQuantity());
        assertEquals(new BigDecimal("49.99"), responseItem1.getPrice());

        // Check second item
        OrderResponseDTO.OrderItemResponse responseItem2 = response.getItems().get(1);
        assertEquals(productId2, responseItem2.getProductId());
        assertEquals(1, responseItem2.getQuantity());
        assertEquals(new BigDecimal("99.99"), responseItem2.getPrice());
    }

    @Test
    void shouldMapOrderWithEmptyItems() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .createdAt(createdAt)
                .items(List.of()) // Empty list
                .build();

        OrderResponseDTO response = MapperFacade.toResponseDTO(order);

        assertNotNull(response);
        assertEquals(orderId, response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getTotal());
        assertEquals(createdAt, response.getCreatedAt());
        assertNotNull(response.getItems());
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void shouldMapOrderWithSingleItem() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        OrderItem item = OrderItem.builder()
                .productId(productId)
                .quantity(5)
                .price(new BigDecimal("25.50"))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CANCELLED)
                .total(new BigDecimal("127.50"))
                .createdAt(createdAt)
                .items(Arrays.asList(item))
                .build();

        OrderResponseDTO response = MapperFacade.toResponseDTO(order);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        
        OrderResponseDTO.OrderItemResponse responseItem = response.getItems().get(0);
        assertEquals(productId, responseItem.getProductId());
        assertEquals(5, responseItem.getQuantity());
        assertEquals(new BigDecimal("25.50"), responseItem.getPrice());
    }

    @Test
    void shouldPreserveAllOrderStatuses() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        for (OrderStatus status : OrderStatus.values()) {
            Order order = Order.builder()
                    .id(orderId)
                    .userId(userId)
                    .status(status)
                    .total(new BigDecimal("100.00"))
                    .createdAt(createdAt)
                    .items(List.of())
                    .build();

            OrderResponseDTO response = MapperFacade.toResponseDTO(order);

            assertEquals(status, response.getStatus());
        }
    }

    @Test
    void shouldHandleHighPrecisionPrices() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        BigDecimal highPrecisionPrice = new BigDecimal("123.456789");
        BigDecimal highPrecisionTotal = new BigDecimal("987.654321");

        OrderItem item = OrderItem.builder()
                .productId(productId)
                .quantity(1)
                .price(highPrecisionPrice)
                .build();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PAID)
                .total(highPrecisionTotal)
                .createdAt(createdAt)
                .items(Arrays.asList(item))
                .build();

        OrderResponseDTO response = MapperFacade.toResponseDTO(order);

        assertEquals(highPrecisionTotal, response.getTotal());
        assertEquals(highPrecisionPrice, response.getItems().get(0).getPrice());
    }

    @Test
    void shouldNotBeInstantiableViaReflection() throws NoSuchMethodException {
        Constructor<OrderMapper> constructor = OrderMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // TODO: Known issue, to be fixed
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    void shouldHandleNullOrderGracefully() {
        assertThrows(NullPointerException.class, () -> {
            MapperFacade.toResponseDTO((Order) null);
        });
    }

    @Test
    void shouldMaintainItemOrder() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        // Create items in specific order
        OrderItem item1 = OrderItem.builder()
                .productId(UUID.randomUUID())
                .quantity(1)
                .price(new BigDecimal("10.00"))
                .build();

        OrderItem item2 = OrderItem.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .price(new BigDecimal("20.00"))
                .build();

        OrderItem item3 = OrderItem.builder()
                .productId(UUID.randomUUID())
                .quantity(3)
                .price(new BigDecimal("30.00"))
                .build();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PAID)
                .total(new BigDecimal("140.00"))
                .createdAt(createdAt)
                .items(Arrays.asList(item1, item2, item3))
                .build();

        OrderResponseDTO response = MapperFacade.toResponseDTO(order);

        assertEquals(3, response.getItems().size());
        assertEquals(1, response.getItems().get(0).getQuantity());
        assertEquals(2, response.getItems().get(1).getQuantity());
        assertEquals(3, response.getItems().get(2).getQuantity());
    }

    @Test
    void shouldCreateImmutableResponse() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.PAID)
                .total(new BigDecimal("100.00"))
                .createdAt(createdAt)
                .items(List.of())
                .build();

        OrderResponseDTO response = MapperFacade.toResponseDTO(order);

        // Response should be a separate object
        assertNotSame(order, response);
        assertEquals(order.getId(), response.getId());
        assertEquals(order.getUserId(), response.getUserId());
        assertEquals(order.getStatus(), response.getStatus());
        assertEquals(order.getTotal(), response.getTotal());
        assertEquals(order.getCreatedAt(), response.getCreatedAt());
    }
}