package com.example.ecommerce.proxy;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.CreateProductRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Caching Proxy that adds Redis-based caching to ProductService.
 * This is a PROXY PATTERN - it controls access and adds caching without changing the core functionality.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceCachingProxy implements ProductServiceInterface {
    
    private final ProductServiceInterface delegate;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String PRODUCTS_LIST_KEY = "products:all";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    private static final Duration LIST_CACHE_TTL = Duration.ofMinutes(5);

    @Override
    public List<Product> findAll() {
        String cacheKey = PRODUCTS_LIST_KEY;
        
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("PROXY: Cache hit for products list");
                return objectMapper.readValue(cached, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));
            }
        } catch (JsonProcessingException e) {
            log.warn("PROXY: Failed to deserialize cached products list", e);
        }
        
        log.debug("PROXY: Cache miss for products list - fetching from delegate");
        List<Product> products = delegate.findAll();
        
        try {
            String serialized = objectMapper.writeValueAsString(products);
            redisTemplate.opsForValue().set(cacheKey, serialized, LIST_CACHE_TTL);
            log.debug("PROXY: Cached products list with {} items", products.size());
        } catch (JsonProcessingException e) {
            log.warn("PROXY: Failed to cache products list", e);
        }
        
        return products;
    }

    @Override
    public Product findById(UUID id) {
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.debug("PROXY: Cache hit for product: {}", id);
                return objectMapper.readValue(cached, Product.class);
            }
        } catch (JsonProcessingException e) {
            log.warn("PROXY: Failed to deserialize cached product: {}", id, e);
        }
        
        log.debug("PROXY: Cache miss for product: {} - fetching from delegate", id);
        Product product = delegate.findById(id);
        
        try {
            String serialized = objectMapper.writeValueAsString(product);
            redisTemplate.opsForValue().set(cacheKey, serialized, CACHE_TTL);
            log.debug("PROXY: Cached product: {}", id);
        } catch (JsonProcessingException e) {
            log.warn("PROXY: Failed to cache product: {}", id, e);
        }
        
        return product;
    }

    @Override
    public Product create(CreateProductRequestDTO request) {
        Product product = delegate.create(request);
        
        // PROXY BEHAVIOR: Invalidate cache after modification
        invalidateListCache();
        cacheProduct(product);
        
        return product;
    }

    @Override
    public Product update(UUID id, Product updated) {
        Product product = delegate.update(id, updated);
        
        // PROXY BEHAVIOR: Update cache after modification
        cacheProduct(product);
        invalidateListCache();
        
        return product;
    }

    @Override
    public void delete(UUID id) {
        delegate.delete(id);
        
        // PROXY BEHAVIOR: Remove from cache after deletion
        String cacheKey = PRODUCT_CACHE_PREFIX + id;
        redisTemplate.delete(cacheKey);
        log.debug("PROXY: Removed product from cache: {}", id);
        
        invalidateListCache();
    }
    
    private void cacheProduct(Product product) {
        try {
            String cacheKey = PRODUCT_CACHE_PREFIX + product.getId();
            String serialized = objectMapper.writeValueAsString(product);
            redisTemplate.opsForValue().set(cacheKey, serialized, CACHE_TTL);
            log.debug("PROXY: Cached product: {}", product.getId());
        } catch (JsonProcessingException e) {
            log.warn("PROXY: Failed to cache product: {}", product.getId(), e);
        }
    }
    
    private void invalidateListCache() {
        redisTemplate.delete(PRODUCTS_LIST_KEY);
        log.debug("PROXY: Invalidated products list cache");
    }
}
