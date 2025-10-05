package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Central facade for accessing all entity mappers in the application.
 * New mappers can be easily added later.
 */
@Component
public final class MapperFacade {

    private static final OrderMapper orderMapper = new OrderMapper();
    private static final ProductMapper productMapper = new ProductMapper();

    private MapperFacade() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static OrderResponseDTO toResponseDTO(@NonNull Order order) {
        return orderMapper.toResponseDTO(order);
    }

    public static ProductResponseDTO toResponseDTO(@NonNull Product product) {
        return productMapper.toResponseDTO(product);
    }
}
