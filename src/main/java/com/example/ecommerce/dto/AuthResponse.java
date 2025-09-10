package com.example.ecommerce.dto;

import lombok.Data;

/**
 * Response payload returned after successful authentication.
 *
 * <p>
 * Contains the JWT token that the client must use for subsequent requests
 * to protect resources.
 * </p>
 */
@Data
public class AuthResponse {
    private String token;
}
