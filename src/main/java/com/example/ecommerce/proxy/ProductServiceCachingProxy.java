package com.example.ecommerce.proxy;

import com.example.ecommerce.domain.Product;
import com.example.ecommerce.dto.request.CreateProductRequestDTO;
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
 *
 * Utilizes caching to optimize performance for frequently accessed products.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceCachingProxy implements ProductServiceContract {
    
    private final ProductServiceContract delegate;
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
        
        // GRANULAR APPROACH: Cache new product, invalidate only list
        cacheProduct(product);        // Cache the new product for future findById()
        invalidateListCache();        // List cache is stale (missing new product)
        
        log.debug("PROXY: Created product {} - cached individually, invalidated list", product.getId());
        return product;
    }

    @Override
    public Product update(UUID id, Product updated) {
        Product product = delegate.update(id, updated);
        
        // GRANULAR APPROACH: Update specific caches
        cacheProduct(product);        // Update individual product cache
        invalidateListCache();        // List cache contains old version
        
        log.debug("PROXY: Updated product {} - refreshed individual cache, invalidated list", id);
        return product;
    }

    @Override
    public void delete(UUID id) {
        delegate.delete(id);
        
        // GRANULAR APPROACH: Remove specific caches
        invalidateProductCache(id);   // Remove individual product cache
        invalidateListCache();        // List cache contains deleted product
        
        log.debug("PROXY: Deleted product {} - removed individual cache, invalidated list", id);
    }
    
    /**
     * Cache a single product.
     */
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
    
    /**
     * Remove a single product from cache.
     */
    private void invalidateProductCache(UUID productId) {
        String cacheKey = PRODUCT_CACHE_PREFIX + productId;
        redisTemplate.delete(cacheKey);
        log.debug("PROXY: Invalidated product cache: {}", productId);
    }
    
    /**
     * Invalidate the products list cache.
     * This is necessary when the list contents change (create/update/delete operations).
     */
    private void invalidateListCache() {
        redisTemplate.delete(PRODUCTS_LIST_KEY);
        log.debug("PROXY: Invalidated products list cache");
    }
}
