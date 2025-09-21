package com.example.ecommerce.service;

import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.proxy.ProductServiceInterface;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.factory.OrderFactory;
import com.example.ecommerce.factory.OrderItemFactory;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for handling customer orders.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductServiceInterface productService;
    private final OrderValidationService orderValidationService;

    /**
     * Creates a new order for a given user based on the provided request.

     * @param userId  the ID of the user placing the order
     * @param request the request containing order items and quantities
     *
     * @return an {@link OrderResponseDTO} representing the created order
     *
     * @throws RuntimeException if product stock is insufficient
     */
    @Transactional
    public OrderResponseDTO createOrder(UUID userId, CreateOrderRequestDTO request) {
        log.info("Creating order for user: {} with {} items", userId, request.getItems().size());
        
        // CHAIN OF RESPONSIBILITY: Validate the order request
        var validationResult = orderValidationService.validateOrderRequest(request);
        if (!validationResult.isValid()) {
            String errorMessage = String.format("Order validation failed: %s", 
                    String.join(", ", validationResult.getErrors()));
            log.error("ORDER VALIDATION FAILED: {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }
        
        log.info("Order validation passed - proceeding with order creation");
        
        BigDecimal total = BigDecimal.ZERO;

        // Create the order first without items
        Order order = OrderFactory.createNewOrder(userId, BigDecimal.ZERO, null);
        log.debug("Created order object for user: {}", userId);

        // Create items and calculate total
        // NOTE: Validation already checked product existence and stock, so this should be safe
        Order finalOrder = order;
        List<OrderItem> items = request.getItems().stream().map(reqItem -> {
            var product = productService.findById(reqItem.getProductId());
            
            // Reserve stock (validation already confirmed availability)
            inventoryService.reserveStock(product.getId(), reqItem.getQuantity());
            log.debug("Reserved {} units of product: {}", reqItem.getQuantity(), product.getId());

            return OrderItemFactory.createNewOrderItem(finalOrder, product.getId(), reqItem.getQuantity(), product.getPrice());
        }).collect(Collectors.toList());

        // Calculate total from items
        total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Set the calculated total and items
        order.setTotal(total);
        order.setItems(items);
        
        log.debug("Order prepared with total: {} and {} items", total, items.size());
        
        // Save the order with all items (cascade will handle the items)
        order = orderRepository.save(order);
        
        log.info("Successfully created order: {} for user: {} with total: {}", 
                order.getId(), userId, total);

        return mapToResponse(order);
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the ID of the user
     *
     * @return a list of {@link OrderResponseDTO} representing the user's orders
     */
    public List<OrderResponseDTO> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponseDTO getById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
    }

    /**
     * Finds orders by status and created before a specific time.
     * This method encapsulates repository access and provides a clean service layer interface.
     *
     * @param status the order status to filter by
     * @param cutoffTime orders created before this time
     *
     * @return list of orders matching the criteria
     */
    public List<Order> findOrdersByStatusAndCreatedBefore(OrderStatus status,
                                                          Instant cutoffTime) {
        log.debug("Finding orders with status {} created before {}", status, cutoffTime);
        return orderRepository.findByStatusAndCreatedAtBefore(status, cutoffTime);
    }

    /**
     * Bulk cancellation of orders with status change notification.
     * This method handles the complete cancellation workflow including observer notifications.
     *
     * @param orders list of orders to cancel
     * @param reason the reason for cancellation
     * @param statusPublisher the publisher to notify observers of status changes
     */
    @Transactional
    public void cancelOrders(List<Order> orders, String reason, 
                           OrderStatusPublisher statusPublisher) {
        log.info("Bulk cancelling {} orders - Reason: {}", orders.size(), reason);
        
        for (Order order : orders) {
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            
            // Save the order
            orderRepository.save(order);
            
            // Notify observers (this will handle inventory release, notifications, etc.)
            statusPublisher.notifyStatusChange(order, oldStatus, OrderStatus.CANCELLED);
            
            log.debug("Cancelled order: {}", order.getId());
        }
        
        log.info("Bulk cancellation completed for {} orders", orders.size());
    }

    /**
     * Finds unpaid orders that are older than the specified time.
     * Business logic method that encapsulates the criteria for "unpaid orders".
     *
     * @param cutoffTime orders created before this time are considered stale
     *
     * @return list of unpaid orders that should be cancelled
     */
    public List<Order> findUnpaidOrdersOlderThan(Instant cutoffTime) {
        log.debug("Finding unpaid orders older than {}", cutoffTime);
        return findOrdersByStatusAndCreatedBefore(OrderStatus.PENDING, cutoffTime);
    }

    /**
     * Maps an {@link Order} entity to its corresponding {@link OrderResponseDTO} DTO.
     *
     * @param order the order entity to map
     *
     * @return the {@link OrderResponseDTO} representation of the order
     */
    private OrderResponseDTO mapToResponse(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .items(order.getItems().stream().map(i ->
                        OrderResponseDTO.OrderItemResponse.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build()
                ).toList())
                .build();
    }
}
