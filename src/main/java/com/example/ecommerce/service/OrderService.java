package com.example.ecommerce.service;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for handling customer orders, including creation
 * and retrieval of orders for a specific user.
 *
 * <p>
 * Handles stock reservation via {@link InventoryService} and product lookups
 * via {@link ProductService}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductService productService;

    /**
     * Creates a new order for a given user based on the provided request.
     *
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
     * @return an {@link OrderResponse} representing the created order
     *
     * @throws RuntimeException if product stock is insufficient
     */
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        // calculate total
        var ref = new Object() {
            BigDecimal total = BigDecimal.ZERO;
        };

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = request.getItems().stream().map(reqItem -> {
            var product = productService.findById(reqItem.getProductId());
            if (product.getStock() < reqItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for "+ product.getName());
            }

            inventoryService.reserveStock(product.getId(), reqItem.getQuantity());

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(reqItem.getQuantity()));
            ref.total = ref.total.add(itemTotal);

            return OrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .quantity(reqItem.getQuantity())
                    .price(product.getPrice())
                    .build();
        }).collect(Collectors.toList());

        order.setItems(items);
        order.setTotal(ref.total);

        orderRepository.save(order);

        return mapToResponse(order);
    }

    /**
     * Retrieves all orders placed by a specific user.
     *
     * @param userId the ID of the user
     *
     * @return a list of {@link OrderResponse} representing the user's orders
     */
    public List<OrderResponse> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Maps an {@link Order} entity to its corresponding {@link OrderResponse} DTO.
     *
     * @param order the order entity to map
     *
     * @return the {@link OrderResponse} representation of the order
     */
    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .items(order.getItems().stream().map(i ->
                        OrderResponse.OrderItemResponse.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build()
                ).toList())
                .build();
    }

}
