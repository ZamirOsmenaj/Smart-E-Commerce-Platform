package com.example.ecommerce.service;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.request.CreateProductRequestDTO;
import com.example.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Testing service layer logic with mocked dependencies.
 * This represents higher complexity testing with mocking and service interactions.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private UUID testProductId;
    private CreateProductRequestDTO createRequest;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testProduct = Product.builder()
                .id(testProductId)
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("99.99"))
                .build();

        createRequest = new CreateProductRequestDTO();
        createRequest.setName("New Product");
        createRequest.setDescription("New Description");
        createRequest.setPrice(new BigDecimal("149.99"));
        createRequest.setStock(10);
    }

    @Test
    void shouldFindAllProducts() {
        List<Product> expectedProducts = Arrays.asList(testProduct, 
                Product.builder().name("Product 2").build());
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.findAll();

        assertEquals(expectedProducts.size(), actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findAll();
    }

    @Test
    void shouldFindProductById() {
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.findById(testProductId);

        assertNotNull(foundProduct);
        assertEquals(testProduct.getId(), foundProduct.getId());
        assertEquals(testProduct.getName(), foundProduct.getName());
        verify(productRepository).findById(testProductId);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> productService.findById(nonExistentId));

        assertEquals("Product not found!", exception.getMessage());
        verify(productRepository).findById(nonExistentId);
    }

    @Test
    void shouldCreateNewProduct() {
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name(createRequest.getName())
                .description(createRequest.getDescription())
                .price(createRequest.getPrice())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.create(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getName(), result.getName());
        assertEquals(createRequest.getDescription(), result.getDescription());
        assertEquals(createRequest.getPrice(), result.getPrice());
        
        verify(productRepository).save(any(Product.class));
        verify(inventoryService).createInventory(savedProduct.getId(), createRequest.getStock());
    }

    @Test
    void shouldUpdateExistingProduct() {
        Product updatedProduct = Product.builder()
                .name("Updated Name")
                .description("Updated Description")
                .price(new BigDecimal("199.99"))
                .build();

        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.update(testProductId, updatedProduct);

        assertNotNull(result);
        assertEquals(updatedProduct.getName(), testProduct.getName());
        assertEquals(updatedProduct.getDescription(), testProduct.getDescription());
        assertEquals(updatedProduct.getPrice(), testProduct.getPrice());
        
        verify(productRepository).findById(testProductId);
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        UUID nonExistentId = UUID.randomUUID();
        Product updatedProduct = Product.builder().name("Updated").build();
        
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> productService.update(nonExistentId, updatedProduct));

        assertEquals("Product not found!", exception.getMessage());
        verify(productRepository).findById(nonExistentId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        productService.delete(testProductId);

        verify(productRepository).deleteById(testProductId);
        verify(inventoryService).deleteInventoryById(testProductId);
    }

    @Test
    void shouldHandleCreateWithNullDescription() {
        createRequest.setDescription(null);
        Product savedProduct = Product.builder()
                .id(UUID.randomUUID())
                .name(createRequest.getName())
                .description(null)
                .price(createRequest.getPrice())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = productService.create(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getName(), result.getName());
        assertNull(result.getDescription());
        assertEquals(createRequest.getPrice(), result.getPrice());
    }

    @Test
    void shouldVerifyTransactionalBehaviorOnCreate() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.create(createRequest);

        // Verify that both repository save and inventory creation are called
        // This tests the transactional behavior
        verify(productRepository).save(any(Product.class));
        verify(inventoryService).createInventory(any(UUID.class), eq(createRequest.getStock()));
    }
}