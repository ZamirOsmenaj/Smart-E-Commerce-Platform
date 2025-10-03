package com.example.ecommerce.service;

import com.example.ecommerce.proxy.ProductServiceContract;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CreateProductRequestDTO;
import com.example.ecommerce.factory.ProductFactory;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for managing product data, including retrieval,
 * creation, update, and deletion.
 */
@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceContract {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    /**
     * Retrieves all products from the repository.
     *
     * @return a list of all {@link Product} entities
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the UUID of the product to retrieve
     *
     * @return the {@link Product} with the given ID
     *
     * @throws RuntimeException if no product with the given ID exists
     */
    @Override
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
    }

    /**
     * Creates a new product and saves it to the repository.
     *
     * @param request the {@link CreateProductRequestDTO} to create
     *
     * @return the saved {@link Product} entity
     */
    @Transactional
    @Override
    public Product create(CreateProductRequestDTO request) {
        Product product = ProductFactory.createNewProduct(request.getName(), request.getDescription(), request.getPrice());
        Product savedProduct = productRepository.save(product);
        inventoryService.createInventory(savedProduct.getId(), request.getStock());
        return savedProduct;
    }

    /**
     * Updates an existing product identified by its ID.
     *
     * @param id      the UUID of the product to update
     * @param updated the {@link Product} object containing updated fields
     *
     * @return the updated {@link Product} entity
     *
     * @throws RuntimeException if no product with the given ID exists
     */
    @Transactional
    @Override
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
     * @param id the UUID of the product to delete
     */
    @Transactional
    @Override
    public void delete(UUID id) {
        productRepository.deleteById(id);
        inventoryService.deleteInventoryById(id);
    }
}
