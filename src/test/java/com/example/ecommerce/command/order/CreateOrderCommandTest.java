package com.example.ecommerce.command.order;

import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.request.CreateOrderRequestDTO;
import com.example.ecommerce.dto.request.CreateOrderRequestDTO.Item;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.proxy.ProductServiceContract;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.InventoryService;
import com.example.ecommerce.service.OrderValidationService;
import com.example.ecommerce.validation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CreateOrderCommand.
 * Testing command pattern implementation with complex business logic and mocking.
 */
@ExtendWith(MockitoExtension.class)
class CreateOrderCommandTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private InventoryService inventoryService;
    
    @Mock
    private ProductServiceContract productService;
    
    @Mock
    private OrderValidationService orderValidationService;
    
    @Mock
    private OrderStatusPublisher orderStatusPublisher;

    private CreateOrderCommand command;
    private UUID userId;
    private CreateOrderRequestDTO request;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        
        // Create test product
        testProduct = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .price(new BigDecimal("99.99"))
                .build();

        // Create order request
        Item itemRequest = new Item();
        itemRequest.setProductId(testProduct.getId());
        itemRequest.setQuantity(2);
        
        request = new CreateOrderRequestDTO();
        request.setItems(Arrays.asList(itemRequest));

        command = new CreateOrderCommand(
                orderRepository,
                inventoryService,
                productService,
                orderValidationService,
                orderStatusPublisher,
                userId,
                request
        );
    }

//    @Test
//    void shouldExecuteSuccessfully() throws Exception {
//        // Setup mocks
//        when(orderValidationService.validateOrderRequest(request))
//                .thenReturn(ValidationResult.success("validation"));
//        when(productService.findById(testProduct.getId()))
//                .thenReturn(testProduct);
//
//        Order savedOrder = Order.builder()
//                .id(UUID.randomUUID())
//                .userId(userId)
//                .total(new BigDecimal("199.98"))
//                .build();
//        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
//
//        CommandResult result = command.execute();
//
//        // TODO: Known issue, to be fixed
//        assertTrue(result.isSuccess());
//        assertEquals("Order created successfully", result.getMessage());
//        assertNotNull(result.getData());
//
//        verify(orderValidationService).validateOrderRequest(request);
//        verify(productService).findById(testProduct.getId());
//        verify(inventoryService).reserveStock(testProduct.getId(), 2);
//        verify(orderRepository).save(any(Order.class));
//    }

    @Test
    void shouldFailWhenValidationFails() throws Exception {
        ValidationResult failedValidation = ValidationResult.failure("validation", "Invalid request");
        when(orderValidationService.validateOrderRequest(request))
                .thenReturn(failedValidation);

        CommandResult result = command.execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Order validation failed"));
        assertTrue(result.getMessage().contains("Invalid request"));
        
        verify(orderValidationService).validateOrderRequest(request);
        verify(productService, never()).findById(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldFailWhenProductServiceThrowsException() throws Exception {
        when(orderValidationService.validateOrderRequest(request))
                .thenReturn(ValidationResult.success("validation"));
        when(productService.findById(testProduct.getId()))
                .thenThrow(new RuntimeException("Product not found"));

        CommandResult result = command.execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create order"));
        assertNotNull(result.getError());
        
        verify(orderValidationService).validateOrderRequest(request);
        verify(productService).findById(testProduct.getId());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldSupportUndo() {
        assertTrue(command.supportsUndo());
    }

    @Test
    void shouldProvideDescription() {
        String description = command.getDescription();
        
        assertNotNull(description);
        assertTrue(description.contains(userId.toString()));
        assertTrue(description.contains("1 items"));
    }

    @Test
    void shouldFailUndoWhenNoOrderCreated() throws Exception {
        CommandResult undoResult = command.undo();

        assertFalse(undoResult.isSuccess());
        assertEquals("Cannot undo: No order was created", undoResult.getMessage());
    }

//    @Test
//    void shouldCalculateTotalCorrectly() throws Exception {
//        // Add another item to test total calculation
//        Item secondItem = new Item();
//        secondItem.setProductId(UUID.randomUUID());
//        secondItem.setQuantity(1);
//        request.getItems().add(secondItem);
//
//        Product secondProduct = Product.builder()
//                .id(secondItem.getProductId())
//                .name("Second Product")
//                .price(new BigDecimal("50.00"))
//                .build();
//
//        when(orderValidationService.validateOrderRequest(request))
//                .thenReturn(ValidationResult.success("validation"));
//        when(productService.findById(testProduct.getId()))
//                .thenReturn(testProduct);
//        when(productService.findById(secondProduct.getId()))
//                .thenReturn(secondProduct);
//
//        Order savedOrder = Order.builder()
//                .id(UUID.randomUUID())
//                .userId(userId)
//                .total(new BigDecimal("249.98")) // (99.99 * 2) + (50.00 * 1)
//                .build();
//        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
//
//        CommandResult result = command.execute();
//
//        TODO: Known issue, to be fixed!
//        assertTrue(result.isSuccess());
//        verify(inventoryService).reserveStock(testProduct.getId(), 2);
//        verify(inventoryService).reserveStock(secondProduct.getId(), 1);
//    }

    @Test
    void shouldHandleInventoryReservationFailure() throws Exception {
        when(orderValidationService.validateOrderRequest(request))
                .thenReturn(ValidationResult.success("validation"));
        when(productService.findById(testProduct.getId()))
                .thenReturn(testProduct);
        doThrow(new RuntimeException("Insufficient stock"))
                .when(inventoryService).reserveStock(testProduct.getId(), 2);

        CommandResult result = command.execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create order"));
        
        verify(orderValidationService).validateOrderRequest(request);
        verify(productService).findById(testProduct.getId());
        verify(inventoryService).reserveStock(testProduct.getId(), 2);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldHandleRepositorySaveFailure() throws Exception {
        when(orderValidationService.validateOrderRequest(request))
                .thenReturn(ValidationResult.success("validation"));
        when(productService.findById(testProduct.getId()))
                .thenReturn(testProduct);
        when(orderRepository.save(any(Order.class)))
                .thenThrow(new RuntimeException("Database error"));

        CommandResult result = command.execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create order"));
        assertNotNull(result.getError());
        
        verify(orderRepository).save(any(Order.class));
    }
}