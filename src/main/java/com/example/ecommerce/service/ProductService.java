package com.example.ecommerce.service;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product create(Product product) {
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public Product update(UUID id, Product updated) {
        Product product = findById(id);
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setPrice(updated.getPrice());
        product.setStock(updated.getStock());
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

}
