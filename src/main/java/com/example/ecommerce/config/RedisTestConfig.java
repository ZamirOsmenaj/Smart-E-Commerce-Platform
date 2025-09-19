package com.example.ecommerce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Test Redis connection on startup
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisTestConfig implements CommandLineRunner {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void run(String... args) throws Exception {
        try {
            // Test Redis connection
            redisTemplate.opsForValue().set("test-key", "test-value");
            String value = redisTemplate.opsForValue().get("test-key");
            
            if ("test-value".equals(value)) {
                log.info("✅ Redis connection is working! Test key/value stored and retrieved successfully.");
            } else {
                log.error("❌ Redis connection issue! Expected 'test-value', got: {}", value);
            }
            
            // Clean up test key
            redisTemplate.delete("test-key");
            
        } catch (Exception e) {
            log.error("❌ Redis connection failed: {}", e.getMessage());
            log.error("Make sure Redis is running on localhost:6379 or update the configuration");
        }
    }
}