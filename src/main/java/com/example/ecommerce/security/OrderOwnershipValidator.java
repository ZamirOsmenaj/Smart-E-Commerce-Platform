package com.example.ecommerce.security;

import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementation of OwnershipValidator for Order resources.
 * Validates that users can only access orders they own.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOwnershipValidator implements OwnershipValidator<OrderResponseDTO> {
    
    private final OrderService orderService;
    
    @Override
    public boolean validateOwnership(UUID userId, UUID orderId) {
        try {
            OrderResponseDTO order = getResource(orderId);
            boolean isOwner = order.getUserId().equals(userId);
            
            if (!isOwner) {
                log.warn("SECURITY: User {} attempted to access order {} owned by {}", 
                        userId, orderId, order.getUserId());
            }
            
            return isOwner;
        } catch (RuntimeException e) {
            log.error("SECURITY: Error validating ownership for order {} by user {}: {}", 
                    orderId, userId, e.getMessage());
            throw new ResourceNotFoundException("Order not found: " + orderId, e);
        }
    }
    
    @Override
    public OrderResponseDTO getResource(UUID orderId) {
        try {
            return orderService.getById(orderId);
        } catch (RuntimeException e) {
            throw new ResourceNotFoundException("Order not found: " + orderId, e);
        }
    }
}