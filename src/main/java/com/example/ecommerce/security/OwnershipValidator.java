package com.example.ecommerce.security;

import java.util.UUID;

/**
 * Generic interface for validating ownership of resources.
 * 
 * @param <T> the type of resource to validate ownership for
 */
public interface OwnershipValidator<T> {
    
    /**
     * Validates that the specified user owns the given resource.
     *
     * @param userId the ID of the user
     * @param resourceId the ID of the resource
     * @return true if the user owns the resource, false otherwise
     * @throws ResourceNotFoundException if the resource doesn't exist
     */
    boolean validateOwnership(UUID userId, UUID resourceId);
    
    /**
     * Gets the resource by ID for ownership validation.
     *
     * @param resourceId the ID of the resource
     * @return the resource
     * @throws ResourceNotFoundException if the resource doesn't exist
     */
    T getResource(UUID resourceId);
}