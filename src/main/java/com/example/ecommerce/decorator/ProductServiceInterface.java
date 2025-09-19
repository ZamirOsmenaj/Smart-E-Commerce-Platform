package com.example.ecommerce.decorator;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CreateProductRequest;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the contract for product service operations.
 * Used by the Decorator pattern to ensure both the original service
 * and its decorators implement the same methods.
 */
public interface ProductServiceInterface {
    
    /**
     * Retrieves all products.
     */
    List<Product> findAll();
    
    /**
     * Retrieves a product by its ID.
     */
    Product findById(UUID id);
    
    /**
     * Creates a new product.
     */
    Product create(CreateProductRequest request);
    
    /**
     * Updates an existing product.
     */
    Product update(UUID id, Product updated);
    
    /**
     * Deletes a product by its ID.
     */
    void delete(UUID id);
}