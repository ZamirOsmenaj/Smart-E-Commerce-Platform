package com.example.ecommerce.dto;

import lombok.Data;

/**
 * Request payload for registering a new user.
 *
 * <p>
 * Contains the user's email and password, which will be validated and
 * persisted during the registration process.
 * </p>
 */
@Data
public class RegisterRequest {
    private String email;
    private String password;
}
