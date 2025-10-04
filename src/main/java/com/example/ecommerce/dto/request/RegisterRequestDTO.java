package com.example.ecommerce.dto.request;

import lombok.Data;

/**
 * Request payload for registering a new user.
 */
@Data
public class RegisterRequestDTO {
    private String email;
    private String password;
}
