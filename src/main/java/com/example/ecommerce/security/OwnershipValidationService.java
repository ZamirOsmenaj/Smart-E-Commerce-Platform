package com.example.ecommerce.security;

import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.constants.MessageConstants;
import com.example.ecommerce.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Central service for handling ownership validation across different resource types.
 * Provides a unified interface for validating user ownership of resources.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OwnershipValidationService {
    
    private final JwtService jwtService;
    private final OrderOwnershipValidator orderOwnershipValidator;
    
    /**
     * Extracts user ID from JWT token.
     *
     * @param authHeader the authorization header containing the Bearer token
     * @return the user ID extracted from the token
     */
    public UUID extractUserIdFromToken(String authHeader) {
        String jwt = authHeader.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        return jwtService.extractUserId(jwt);
    }
    
    /**
     * Validates that the user from the token owns the specified order.
     *
     * @param authHeader the authorization header containing the Bearer token
     * @param orderId the ID of the order to validate ownership for
     * @throws OwnershipValidationException if the user doesn't own the order
     * @throws ResourceNotFoundException if the order doesn't exist
     */
    public void validateOrderOwnership(String authHeader, UUID orderId) {
        UUID userId = extractUserIdFromToken(authHeader);
        validateOrderOwnership(userId, orderId);
    }
    
    /**
     * Validates that the specified user owns the specified order.
     *
     * @param userId the ID of the user
     * @param orderId the ID of the order to validate ownership for
     * @throws OwnershipValidationException if the user doesn't own the order
     * @throws ResourceNotFoundException if the order doesn't exist
     */
    public void validateOrderOwnership(UUID userId, UUID orderId) {
        if (!orderOwnershipValidator.validateOwnership(userId, orderId)) {
            throw new OwnershipValidationException(
                String.format("User %s %s %s", userId, MessageConstants.USER_DOES_NOT_OWN_ORDER.toLowerCase().replace("user does not own this order", "does not own order"), orderId)
            );
        }
        log.debug("SECURITY: Ownership validation successful for user {} and order {}", userId, orderId);
    }
}