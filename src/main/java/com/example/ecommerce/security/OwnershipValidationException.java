package com.example.ecommerce.security;

/**
 * Exception thrown when ownership validation fails.
 */
public class OwnershipValidationException extends RuntimeException {
    
    public OwnershipValidationException(String message) {
        super(message);
    }
    
    public OwnershipValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}