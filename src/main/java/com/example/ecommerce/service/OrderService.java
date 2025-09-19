package com.example.ecommerce.service;

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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for handling customer orders, including creation
 * and retrieval of orders for a specific user.
 * <p>
 * Handles stock reservation via {@link InventoryService} and product lookups
 * via {@link ProductService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductServiceInterface productService;

    /**
     * Creates a new order for a given user based on the provided request.
     * <p>
     * Steps:
     * <ul>
     *     <li>Validates product availability using {@link ProductService}.</li>
     *     <li>Reserves stock for each item via {@link InventoryService}.</li>
     *     <li>Calculates the total cost of the order.</li>
     *     <li>Saves the order and its items in the database.</li>
     * </ul>
     * </p>
     *
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
        
        BigDecimal total = BigDecimal.ZERO;

        // Create the order first without items
        Order order = OrderFactory.createNewOrder(userId, BigDecimal.ZERO, null);
        log.debug("Created order object for user: {}", userId);

        // Create items and calculate total
        Order finalOrder = order;
        List<OrderItem> items = request.getItems().stream().map(reqItem -> {
            var product = productService.findById(reqItem.getProductId());
            var inventory = inventoryService.findById(reqItem.getProductId());

            if (inventory.getAvailable() < reqItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for "+ product.getName());
            }

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
