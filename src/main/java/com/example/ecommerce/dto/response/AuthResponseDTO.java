package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response payload returned after successful authentication.
 *
 * Contains the JWT token that the client must use for subsequent requests
 * to protect resources.
 */
@Data
@Builder
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
}
