package com.example.ecommerce.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for Product domain entity.
 * Testing basic object creation, getters/setters, and builder pattern.
 */
class ProductTest {

    @Test
    void shouldCreateProductWithNoArgsConstructor() {
        Product product = new Product();
        
        assertNotNull(product);
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getDescription());
        assertNull(product.getPrice());
    }

    @Test
    void shouldCreateProductWithAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        
        Product product = new Product(id, name, description, price);
        
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
    }

    @Test
    void shouldCreateProductWithBuilder() {
        UUID id = UUID.randomUUID();
        String name = "Builder Product";
        BigDecimal price = new BigDecimal("149.99");
        
        Product product = Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
        
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertNull(product.getDescription());
    }

    @Test
    void shouldSetAndGetProperties() {
        Product product = new Product();
        UUID id = UUID.randomUUID();
        String name = "Setter Product";
        String description = "Updated Description";
        BigDecimal price = new BigDecimal("199.99");
        
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        
        assertEquals(id, product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
    }

    @Test
    void shouldHandleNullDescription() {
        Product product = Product.builder()
                .name("Product with null description")
                .price(new BigDecimal("50.00"))
                .description(null)
                .build();
        
        assertNull(product.getDescription());
        assertNotNull(product.getName());
        assertNotNull(product.getPrice());
    }

    @Test
    void shouldImplementSerializable() {
        assertTrue(Product.class.getInterfaces().length > 0);
        assertEquals("java.io.Serializable", 
                Product.class.getInterfaces()[0].getName());
    }
}