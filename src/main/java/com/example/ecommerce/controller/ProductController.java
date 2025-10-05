package com.example.ecommerce.controller;

import com.example.ecommerce.mapper.MapperFacade;
import com.example.ecommerce.proxy.ProductServiceContract;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.request.CreateProductRequestDTO;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

/**
 * Controller that manages product operations such as retrieval, creation, update, and deletion.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductServiceContract productService;

    /**
     * Retrieves all products.
     *
     * @return a standardized API response containing a list of all products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAll() {
        List<Product> products = productService.findAll();
        List<ProductResponseDTO> productDTOs = products.stream()
                .map(MapperFacade::toResponseDTO)
                .collect(Collectors.toList());
        
        log.debug("PRODUCT CONTROLLER: Retrieved {} products", productDTOs.size());
        return ResponseEntity.ok(ApiResponse.success(productDTOs, "Products retrieved successfully"));
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the UUID of the product
     * @return a standardized API response containing the product
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(@PathVariable UUID id) {
        Product product = productService.findById(id);
        ProductResponseDTO productDTO = MapperFacade.toResponseDTO(product);
        
        log.debug("PRODUCT CONTROLLER: Retrieved product with ID: {}", id);
        return ResponseEntity.ok(ApiResponse.success(productDTO, "Product retrieved successfully"));
    }

    /**
     * Creates a new product.
     *
     * @param request the product details to create
     * @return a standardized API response containing the created product
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(@RequestBody CreateProductRequestDTO request) {
        try {
            Product product = productService.create(request);
            ProductResponseDTO productDTO = MapperFacade.toResponseDTO(product);
            
            log.info("PRODUCT CONTROLLER: Product created successfully with ID: {}", product.getId());
            return ResponseEntity.ok(ApiResponse.success(productDTO, "Product created successfully"));
        } catch (Exception e) {
            log.error("PRODUCT CONTROLLER: Product creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "PRODUCT_CREATION_FAILED"));
        }
    }

    /**
     * Updates an existing product by its ID.
     *
     * @param id the UUID of the product to update
     * @param product the updated product details
     * @return a standardized API response containing the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> update(@PathVariable UUID id, @RequestBody Product product) {
        try {
            Product updatedProduct = productService.update(id, product);
            ProductResponseDTO productDTO = MapperFacade.toResponseDTO(updatedProduct);
            
            log.info("PRODUCT CONTROLLER: Product updated successfully with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(productDTO, "Product updated successfully"));
        } catch (Exception e) {
            log.error("PRODUCT CONTROLLER: Product update failed for ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "PRODUCT_UPDATE_FAILED"));
        }
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the UUID of the product to delete
     * @return a standardized API response confirming deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        try {
            productService.delete(id);
            
            log.info("PRODUCT CONTROLLER: Product deleted successfully with ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
        } catch (Exception e) {
            log.error("PRODUCT CONTROLLER: Product deletion failed for ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "PRODUCT_DELETION_FAILED"));
        }
    }
}
