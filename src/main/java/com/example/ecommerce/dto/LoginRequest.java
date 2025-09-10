package com.example.ecommerce.dto;

import lombok.Data;

/**
 * Request payload for authenticating a user.
 *
 * <p>
 * Contains the email and password credentials required to perform login
 * and issue an authentication token if valid.
 * </p>
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}
