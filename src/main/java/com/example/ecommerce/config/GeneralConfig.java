package com.example.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * General application configuration.
 */
@Configuration
public class GeneralConfig {

    /**
     * Creates a {@link RestTemplate} bean for performing HTTP requests.
     *
     * This bean can be injected wherever HTTP communication with external
     * services is required.
     *
     * @return a new {@link RestTemplate} instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
