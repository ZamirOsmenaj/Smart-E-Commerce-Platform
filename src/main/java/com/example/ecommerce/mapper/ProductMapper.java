package com.example.ecommerce.mapper;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import lombok.NonNull;
import org.springframework.stereotype.Component;

/**
 * Mapper responsible for converting {@link Product} entities
 * into {@link ProductResponseDTO} objects.
 */
@Component
public class ProductMapper implements Mapper<Product, ProductResponseDTO> {

    @Override
    public ProductResponseDTO toResponseDTO(@NonNull Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}

