package com.example.ecommerce.command.order;

import com.example.ecommerce.command.Command;
import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.factory.OrderFactory;
import com.example.ecommerce.factory.OrderItemFactory;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.proxy.ProductServiceContract;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.InventoryService;
import com.example.ecommerce.service.OrderValidationService;
import com.example.ecommerce.utils.OrderMapperUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Command for creating a new order.
 * Contains the complete order creation business logic and supports undo operations.
 */
@RequiredArgsConstructor
@Slf4j
public class CreateOrderCommand implements Command {
    
    // Direct dependencies - no OrderService needed
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductServiceContract productService;
    private final OrderValidationService orderValidationService;
    private final OrderStatusPublisher orderStatusPublisher;
    
    // Command parameters
    private final UUID userId;
    private final CreateOrderRequestDTO request;
    
    // Store the created order for potential undo
    private Order createdOrder;
    
    @Override
    @Transactional
    public CommandResult execute() throws Exception {
        try {
            log.info("COMMAND: Executing CreateOrderCommand for user: {} with {} items", userId, request.getItems().size());
            
            // CHAIN OF RESPONSIBILITY: Validate the order request
            var validationResult = orderValidationService.validateOrderRequest(request);
            if (!validationResult.isValid()) {
                String errorMessage = String.format("Order validation failed: %s", 
                        String.join(", ", validationResult.getErrors()));
                log.error("COMMAND: ORDER VALIDATION FAILED: {}", errorMessage);
                return CommandResult.failure(errorMessage);
            }
            
            log.info("COMMAND: Order validation passed - proceeding with order creation");
            
            // Create the order first without items
            Order order = OrderFactory.createNewOrder(userId, BigDecimal.ZERO, null);
            log.debug("COMMAND: Created order object for user: {}", userId);

            // Create items and calculate total
            List<OrderItem> items = request.getItems().stream().map(reqItem -> {
                // Get product (validation already confirmed product existence)
                var product = productService.findById(reqItem.getProductId());
                
                // Reserve stock (validation already confirmed availability)
                inventoryService.reserveStock(product.getId(), reqItem.getQuantity());
                log.debug("COMMAND: Reserved {} units of product: {}", reqItem.getQuantity(), product.getId());

                return OrderItemFactory.createNewOrderItem(order, product.getId(), reqItem.getQuantity(), product.getPrice());
            }).collect(Collectors.toList());

            // Calculate total from items
            BigDecimal total = items.stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Set the calculated total and items
            order.setTotal(total);
            order.setItems(items);
            
            log.debug("COMMAND: Order prepared with total: {} and {} items", total, items.size());
            
            // Save the order with all items (cascade will handle the items)
            this.createdOrder = orderRepository.save(order);
            
            log.info("COMMAND: Successfully created order: {} for user: {} with total: {}", 
                    createdOrder.getId(), userId, total);

            OrderResponseDTO response = OrderMapperUtils.toResponse(createdOrder);
            return CommandResult.success("Order created successfully", response);
            
        } catch (Exception e) {
            log.error("COMMAND: Failed to create order for user: {} - Error: {}", userId, e.getMessage());
            return CommandResult.failure("Failed to create order: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public CommandResult undo() throws Exception {
        if (createdOrder == null) {
            return CommandResult.failure("Cannot undo: No order was created");
        }
        
        try {
            log.info("COMMAND: Undoing CreateOrderCommand - cancelling order: {}", createdOrder.getId());
            
            // Create and execute a cancel command for the undo
            var cancelCommand = new CancelOrderCommand(
                orderRepository, 
                orderStatusPublisher, 
                createdOrder.getId(), 
                "Order creation undone"
            );
            
            CommandResult cancelResult = cancelCommand.execute();
            
            if (cancelResult.isSuccess()) {
                log.info("COMMAND: Successfully undone order creation by cancelling order: {}", createdOrder.getId());
                return CommandResult.success("Order creation undone successfully", cancelResult.getData());
            } else {
                log.error("COMMAND: Failed to undo order creation - cancel failed: {}", cancelResult.getMessage());
                return CommandResult.failure("Failed to undo order creation: " + cancelResult.getMessage());
            }
            
        } catch (Exception e) {
            log.error("COMMAND: Failed to undo order creation for order: {} - Error: {}", createdOrder.getId(), e.getMessage());
            return CommandResult.failure("Failed to undo order creation: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean supportsUndo() {
        return true;
    }
    
    @Override
    public String getDescription() {
        return String.format("Create order for user: %s with %d items", 
                userId, request.getItems().size());
    }
}