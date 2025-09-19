package com.example.ecommerce.config;

import com.example.ecommerce.decorator.CachedProductServiceDecorator;
import com.example.ecommerce.decorator.ProductServiceInterface;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Configuration for decorator pattern implementations.
 */
@Configuration
public class DecoratorConfig {
    
    /**
     * Creates a cached product service decorator when caching is enabled.
     * This decorator wraps the original ProductService with Redis caching capabilities.
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.caching.enabled", havingValue = "true", matchIfMissing = true)
    public ProductServiceInterface cachedProductService(
            ProductService productService,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        
        // Wrap the original service with caching decorator
        return new CachedProductServiceDecorator(
                productService, // ProductService now implements ProductServiceInterface directly
                redisTemplate, 
                objectMapper
        );
    }
    
    /**
     * Fallback bean when caching is disabled - just return the original service.
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.caching.enabled", havingValue = "false")
    public ProductServiceInterface directProductService(ProductService productService) {
        return productService; // No adapter needed anymore!
    }
}