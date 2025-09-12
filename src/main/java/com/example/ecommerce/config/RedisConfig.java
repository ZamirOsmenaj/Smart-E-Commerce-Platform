package com.example.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Configuration class for Redis caching integration.
 *
 * <p>
 * Sets up a Redis connection factory using Lettuce and enables
 * Spring's caching abstraction.
 * </p>
 *
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * The hostname of the Redis server, injected from application properties.
     */
    @Value("${spring.redis.host}")
    private String hostName;

    /**
     * The port number of the Redis server, injected from application properties.
     */
    @Value("${spring.redis.port}")
    private int port;

    /**
     * Creates a {@link LettuceConnectionFactory} for connecting to a standalone Redis instance.
     *
     * <p>
     * This connection factory will be used by Spring's caching infrastructure.
     * </p>
     *
     * @return a configured {@link LettuceConnectionFactory} instance
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(hostName);
        redisStandaloneConfiguration.setPort(port);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
}
