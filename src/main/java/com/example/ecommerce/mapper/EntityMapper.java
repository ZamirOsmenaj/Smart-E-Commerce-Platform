package com.example.ecommerce.mapper;


import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Utility class that provides methods for converting domain entities
 * (such as {@link Order} and {@link Product}) into their corresponding
 * response DTOs for API responses.
 *
 * All methods in this class are {@code static}, so it can be used
 * directly without instantiation:
 *
 * This class is stateless and thread-safe.
 */
@Component
public final class EntityMapper {

    /**
     * Private constructor to prevent instantiation.
     */
    private EntityMapper() {}

    /**
     * Converts an {@link Order} entity into an {@link OrderResponseDTO}.
     *
     * @param order the {@link Order} entity to convert (must not be {@code null})
     * @return a corresponding {@link OrderResponseDTO} containing order data
     */
    public static OrderResponseDTO toResponseDTO(Order order) {
        return OrderResponseDTO.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(i -> OrderResponseDTO.OrderItemResponse.builder()
                                .productId(i.getProductId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    /**
     * Converts a {@link Product} entity into a {@link ProductResponseDTO}.
     *
     * @param product the {@link Product} entity to convert (must not be {@code null})
     * @return a corresponding {@link ProductResponseDTO} containing product data
     */
    public static ProductResponseDTO toResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
