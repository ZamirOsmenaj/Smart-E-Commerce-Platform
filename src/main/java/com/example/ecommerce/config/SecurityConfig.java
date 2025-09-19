package com.example.ecommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for application security.
 *
 * <p>
 * Defines authentication and authorization rules, JWT filtering,
 * and password encoding strategy for the application.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Custom JWT authentication filter used to validate tokens
     * on incoming requests.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain for the application.
     *
     * <p>
     * - Disables CSRF protection (since the app likely uses JWT-based authentication).<br>
     * - Allows unauthenticated access to {@code /api/auth/**} endpoints.<br>
     * - Allows unauthenticated access to {@code /api/mock-payments/**} endpoint. <br>
     * - Requires authentication for all other endpoints.<br>
     * - Registers the {@link JwtAuthenticationFilter} to process requests
     *   before {@link UsernamePasswordAuthenticationFilter}.
     * </p>
     *
     * @param http the {@link HttpSecurity} to configure
     *
     * @return the configured {@link SecurityFilterChain}
     *
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean, allowing it to be
     * injected where authentication is required (e.g., in controllers or services).
     *
     * @param config the {@link AuthenticationConfiguration} provided by Spring Security
     *
     * @return the authentication manager
     *
     * @throws Exception if retrieval fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the password encoder used for hashing and verifying user passwords.
     *
     * <p>
     * Uses {@link BCryptPasswordEncoder}, which applies the BCrypt hashing algorithm.
     * </p>
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
