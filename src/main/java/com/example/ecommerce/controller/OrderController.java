package com.example.ecommerce.controller;

import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.service.JwtService;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    @PostMapping
    public OrderResponse createOrder(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @RequestBody CreateOrderRequest request
            ) {
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        return orderService.createOrder(userId, request);
    }

    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token
    ) {
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        return orderService.getOrdersByUser(userId);
    }
}
