package com.example.ecommerce.service;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.factory.ProductFactory;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing product data, including retrieval,
 * creation, update, and deletion.
 *
 * <p>
 * Utilizes caching to optimize performance for frequently accessed products.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    /**
     * Retrieves all products from the repository.
     *
     * @return a list of all {@link Product} entities
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * <p>
     * The result is cached using the product ID as the cache key.
     * </p>
     *
     * @param id the UUID of the product to retrieve
     *
     * @return the {@link Product} with the given ID
     *
     * @throws RuntimeException if no product with the given ID exists
     */
    @Cacheable(value = "products", key = "#id")
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
    }

    /**
     * Creates a new product and saves it to the repository.
     *
     * <p>
     * Evicts all entries in the "products" cache to ensure consistency.
     * </p>
     *
     * @param request the {@link CreateProductRequest} to create
     *
     * @return the saved {@link Product} entity
     */
    @CacheEvict(value = "products", allEntries = true)
    public Product create(CreateProductRequest request) {
        Product product = ProductFactory.createNewProduct(request.getName(), request.getDescription(), request.getPrice());
        Product savedProduct = productRepository.save(product);
        inventoryService.createInventory(savedProduct.getId(), request.getStock());
        return savedProduct;
    }

    /**
     * Updates an existing product identified by its ID.
     *
     * <p>
     * Evicts the cache entry for the updated product to keep cached data consistent.
     * </p>
     *
     * @param id      the UUID of the product to update
     * @param updated the {@link Product} object containing updated fields
     *
     * @return the updated {@link Product} entity
     *
     * @throws RuntimeException if no product with the given ID exists
     */
    @CacheEvict(value = "products", key = "#id")
    public Product update(UUID id, Product updated) {
        Product product = findById(id);
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        return productRepository.save(product);
    }

    /**
     * Deletes a product identified by its ID.
     *
     * <p>
     * Evicts the cache entry for the deleted product to maintain cache consistency.
     * </p>
     *
     * @param id the UUID of the product to delete
     */
    @CacheEvict(value = "products", key = "#id")
    public void delete(UUID id) {
        productRepository.deleteById(id);
        inventoryService.deleteInventoryById(id);
    }

}
