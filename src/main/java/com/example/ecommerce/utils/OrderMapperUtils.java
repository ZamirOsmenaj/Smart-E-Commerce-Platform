package com.example.ecommerce.utils;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.response.OrderResponseDTO;

/**
 * Utility class which maps an {@link Order} entity to its corresponding {@link OrderResponseDTO}.
 */
public class OrderMapperUtils {

    private OrderMapperUtils() {} // prevent instantiation

    public static OrderResponseDTO toResponse (Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
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
