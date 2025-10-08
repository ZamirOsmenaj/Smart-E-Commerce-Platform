package com.example.ecommerce.factory;

import com.example.ecommerce.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductFactory.
 * Testing factory method behavior and product creation.
 */
class ProductFactoryTest {

    @Test
    void shouldCreateNewProductWithAllFields() {
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        
        Product product = ProductFactory.createNewProduct(name, description, price);
        
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertNull(product.getId()); // ID should be null for new products
    }

    @Test
    void shouldCreateProductWithNullDescription() {
        String name = "Product Without Description";
        BigDecimal price = new BigDecimal("49.99");
        
        Product product = ProductFactory.createNewProduct(name, null, price);
        
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertNull(product.getDescription());
        assertEquals(price, product.getPrice());
    }

    @Test
    void shouldCreateProductWithEmptyDescription() {
        String name = "Product With Empty Description";
        String description = "";
        BigDecimal price = new BigDecimal("29.99");
        
        Product product = ProductFactory.createNewProduct(name, description, price);
        
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertTrue(product.getDescription().isEmpty());
        assertEquals(price, product.getPrice());
    }

    @Test
    void shouldCreateProductWithZeroPrice() {
        String name = "Free Product";
        String description = "This product is free";
        BigDecimal price = BigDecimal.ZERO;
        
        Product product = ProductFactory.createNewProduct(name, description, price);
        
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(BigDecimal.ZERO, product.getPrice());
    }

    @Test
    void shouldCreateProductWithHighPrecisionPrice() {
        String name = "Precision Product";
        String description = "Product with high precision price";
        BigDecimal price = new BigDecimal("123.456789");
        
        Product product = ProductFactory.createNewProduct(name, description, price);
        
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(6, product.getPrice().scale());
    }

    @Test
    void shouldCreateMultipleDistinctProducts() {
        Product product1 = ProductFactory.createNewProduct("Product 1", "Description 1", new BigDecimal("10.00"));
        Product product2 = ProductFactory.createNewProduct("Product 2", "Description 2", new BigDecimal("20.00"));
        
        assertNotNull(product1);
        assertNotNull(product2);
        assertNotEquals(product1, product2);
        assertNotEquals(product1.getName(), product2.getName());
        assertNotEquals(product1.getPrice(), product2.getPrice());
    }

    @Test
    void shouldHandleNullName() {
        String description = "Product with null name";
        BigDecimal price = new BigDecimal("15.00");
        
        Product product = ProductFactory.createNewProduct(null, description, price);
        
        assertNotNull(product);
        assertNull(product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
    }

    @Test
    void shouldHandleNullPrice() {
        String name = "Product with null price";
        String description = "This product has no price";
        
        Product product = ProductFactory.createNewProduct(name, description, null);
        
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertNull(product.getPrice());
    }
}