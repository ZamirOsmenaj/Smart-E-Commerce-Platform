package com.example.ecommerce.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Service responsible for creating and validating JSON Web Tokens (JWTs).
 *
 * <p>
 * Handles generating tokens, extracting the subject from a token,
 * and converting the subject into a {@link UUID}.
 * </p>
 */
@Service
public class JwtService {

    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    private final String secret;
    private final long expiration;

    private Key key;

    /**
     * Constructs a new {@link JwtService} with the provided secret and expiration time.
     *
     * @param secret     the JWT signing secret, injected from configuration
     * @param expiration the token expiration time in milliseconds, injected from configuration
     */
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    /**
     * Initializes the JWT signing key after construction.
     *
     * <p>
     * Validates that the secret exists and is at least 32 bytes long
     * (required for HS256 signing). Throws {@link IllegalArgumentException}
     * if validation fails.
     * </p>
     */
    @PostConstruct
    public void init() {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret is missing! Please set 'jwt.secret' or env var JWT_SECRET.");
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret is too short! It must be at least 32 bytes (256 bits) for HS256. " +
                            "Current length: " + keyBytes.length + " bytes."
            );
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
        logger.info("✅ JwtService initialized successfully with a valid secret (" + keyBytes.length + " bytes)");
    }

    /**
     * Generates a JWT token for the specified subject.
     *
     * @param subject the subject to include in the token (e.g., user ID)
     *
     * @return the generated JWT as a {@link String}
     */
    public String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the subject from a given JWT token.
     *
     * @param token the JWT token
     *
     * @return the subject contained in the token
     */
    public String extractSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the user ID from a JWT token.
     *
     * @param token the JWT token
     *
     * @return the user ID as a {@link UUID}
     */
    public UUID extractUserId(String token) {
        return UUID.fromString(extractSubject(token));
    }

}
