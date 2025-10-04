package com.example.ecommerce.dto.request;

import lombok.Data;

/**
 * Request payload for authenticating a user.
 */
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
