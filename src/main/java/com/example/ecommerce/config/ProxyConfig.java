package com.example.ecommerce.config;

import com.example.ecommerce.proxy.ProductServiceContract;
import com.example.ecommerce.proxy.ProductServiceCachingProxy;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Configuration for Proxy Pattern implementations.
 * This handles the selection between proxied and direct service access.
 */
@Configuration
public class ProxyConfig {
    
    /**
     * PROXY PATTERN: Caching proxy enabled
     * This wraps the ProductService with caching capabilities
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.caching.enabled", havingValue = "true", matchIfMissing = true)
    public ProductServiceContract cachingProxy(
            ProductService productService,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        
        System.out.println("🚀 CREATING ProductServiceCachingProxy - Caching is ENABLED");
        return new ProductServiceCachingProxy(productService, redisTemplate, objectMapper);
    }
    
    /**
     * Direct service access (no proxy)
     * Used when caching is disabled
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.caching.enabled", havingValue = "false")
    public ProductServiceContract directProductService(ProductService productService) {
        System.out.println("🚀 CREATING Direct ProductService - Caching is DISABLED");
        return productService;
    }
}