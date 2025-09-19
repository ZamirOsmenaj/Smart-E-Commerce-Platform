package com.example.ecommerce.controller;

import com.example.ecommerce.proxy.ProductServiceInterface;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CreateProductRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller that manages product operations such as retrieval, creation, update, and deletion.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceInterface productService;

    /**
     * Retrieves all products.
     *
     * @return a list of all {@link Product} objects.
     */
    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the UUID of the product
     *
     * @return the {@link Product} with the given ID
     */
    @GetMapping("/{id}")
    public Product getById(@PathVariable UUID id) {
        return productService.findById(id);
    }

    /**
     * Creates a new product.
     *
     * @param request the product details to create
     *
     * @return the created {@link Product}
     */
    @PostMapping
    public Product create(@RequestBody CreateProductRequestDTO request) {
        return productService.create(request);
    }

    /**
     * Updates an existing product by its ID.
     *
     * @param id the UUID of the product to update
     * @param product the updated product details
     *
     * @return the updated {@link Product}
     */
    @PutMapping("/{id}")
    public Product update(@PathVariable UUID id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the UUID of the product to delete
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.delete(id);
    }
}
