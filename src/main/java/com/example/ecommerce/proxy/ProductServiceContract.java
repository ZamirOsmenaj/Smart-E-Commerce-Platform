package com.example.ecommerce.proxy;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.request.CreateProductRequestDTO;

import java.util.List;
import java.util.UUID;

/**
 * Interface defining the contract for product service operations.
 * Used by the Proxy pattern to ensure both the original service
 * and its proxies implement the same methods.
 */
public interface ProductServiceContract {
    
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
    Product create(CreateProductRequestDTO request);
    
    /**
     * Updates an existing product.
     */
    Product update(UUID id, Product updated);
    
    /**
     * Deletes a product by its ID.
     */
    void delete(UUID id);
}
