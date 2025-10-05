package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting {@link Order} entities
 * into {@link OrderResponseDTO} objects.
 */
@Component
public class OrderMapper implements Mapper<Order, OrderResponseDTO> {

    @Override
    public OrderResponseDTO toResponseDTO(@NonNull Order order) {
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
