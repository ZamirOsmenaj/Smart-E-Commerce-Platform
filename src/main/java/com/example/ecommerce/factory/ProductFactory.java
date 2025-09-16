package com.example.ecommerce.factory;

import com.example.ecommerce.domain.Product;

import java.math.BigDecimal;

/**
 * Factory responsible for creating {@link Product} instances.
 */
public class ProductFactory {

    public static Product createNewProduct(String name, String description, BigDecimal price) {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();
    }
}
