package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import org.springframework.stereotype.Component;

/**
 * Utility class which maps an {@link Order} entity to its corresponding {@link OrderResponseDTO}.
 */
@Component
public class OrderMapper {

    private OrderMapper() {} // prevent instantiation

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
