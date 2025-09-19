package com.example.ecommerce.controller;

import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.service.JwtService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller that manages customer orders.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;
    private final PaymentService paymentService;

    /**
     * Creates a new order for the authenticated user.
     *
     * @param token the JWT authorization header containing the Bearer token
     * @param request the request containing order details
     *
     * @return an {@link OrderResponseDTO} representing the created order
     */
    @PostMapping
    public OrderResponseDTO createOrder(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @RequestBody CreateOrderRequestDTO request) {

        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        return orderService.createOrder(userId, request);
    }

    /**
     * Retrieves all orders for the authenticated user.
     *
     * @param token the JWT authorization header containing the Bearer token
     *
     * @return a list of {@link OrderResponseDTO} objects representing the user's orders
     */
    @GetMapping
    public List<OrderResponseDTO> getOrders(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {

        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        return orderService.getOrdersByUser(userId);
    }
}
