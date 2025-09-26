package com.example.ecommerce.controller;

import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.service.JwtService;
import com.example.ecommerce.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller that manages customer orders using the Command Pattern.
 * Commands encapsulate operations and provide undo capabilities where applicable.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    /**
     * Retrieves all orders for the authenticated user.
     *
     * @param token the JWT authorization header containing the Bearer token
     * @return a list of {@link OrderResponseDTO} objects representing the user's orders
     */
    @GetMapping
    public List<OrderResponseDTO> getOrders(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {

        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        return orderService.getOrdersByUser(userId);
    }

    /**
     * Creates a new order for the authenticated user using Command Pattern.
     *
     * @param token the JWT authorization header containing the Bearer token
     * @param request the request containing order details
     * @return an {@link OrderResponseDTO} representing the created order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @RequestBody CreateOrderRequestDTO request) {

        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        
        // COMMAND PATTERN: Use integrated OrderService command method
        CommandResult result = orderService.createOrderWithCommand(userId, request);
        
        if (result.isSuccess()) {
            log.info("ORDER CONTROLLER: Order created successfully via command pattern");
            return ResponseEntity.ok(result.getData());
        } else {
            log.error("ORDER CONTROLLER: Order creation failed: {}", result.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(result.getMessage()));
        }
    }

    /**
     * Cancels an order using Command Pattern.
     * 
     * COMMAND PATTERN: Encapsulates order cancellation as a command.
     * STATE PATTERN: Uses state validation for order cancellation.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @param orderId the ID of the order to cancel
     * @param request the cancellation request containing the reason
     * @return the updated order response
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @PathVariable UUID orderId,
            @RequestBody CancellationRequest request) {
        
        log.info("ORDER CONTROLLER: Received cancel request for order {} with reason: {}", orderId, request.getReason());
        
        // Validate user owns the order
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        
        try {
            // Check if user owns the order
            OrderResponseDTO existingOrder = orderService.getById(orderId);
            if (!existingOrder.getUserId().equals(userId)) {
                log.warn("ORDER CONTROLLER: User {} attempted to cancel order {} owned by {}", 
                        userId, orderId, existingOrder.getUserId());
                return ResponseEntity.status(403).body(new ErrorResponse("You do not own this order!"));
            }
            
            // COMMAND PATTERN: Use integrated OrderService command method
            CommandResult result = orderService.cancelOrderWithCommand(orderId, request.getReason());
            
            if (result.isSuccess()) {
                log.info("ORDER CONTROLLER: Successfully cancelled order {} via command pattern", orderId);
                return ResponseEntity.ok(result.getData());
            } else {
                log.warn("ORDER CONTROLLER: Order cancellation failed: {}", result.getMessage());
                return ResponseEntity.badRequest().body(new ErrorResponse(result.getMessage()));
            }
            
        } catch (RuntimeException e) {
            // Order not found or other runtime error
            log.error("ORDER CONTROLLER: Runtime error for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets available actions for an order in its current state.
     * 
     * STATE PATTERN: Shows what operations are allowed for the order.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @param orderId the ID of the order
     * @return available actions description
     */
    @GetMapping("/{orderId}/available-actions")
    public ResponseEntity<?> getAvailableActions(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @PathVariable UUID orderId) {
        
        log.info("ORDER CONTROLLER: Received available-actions request for order {}", orderId);
        
        // Validate user owns the order
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        
        try {
            // Check if user owns the order
            OrderResponseDTO existingOrder = orderService.getById(orderId);
            if (!existingOrder.getUserId().equals(userId)) {
                log.warn("ORDER CONTROLLER: User {} attempted to access order {} owned by {}", 
                        userId, orderId, existingOrder.getUserId());
                return ResponseEntity.status(403).body(new ErrorResponse("You do not own this order!"));
            }
            
            String actions = orderService.getOrderAvailableActions(orderId);
            AvailableActionsResponse response = new AvailableActionsResponse(orderId, actions);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("ORDER CONTROLLER: Error getting available actions for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Checks if an order can transition to a specific status.
     * 
     * STATE PATTERN: Validates state transitions.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @param orderId the ID of the order
     * @param targetStatus the target status to check
     * @return whether the transition is allowed
     */
    @GetMapping("/{orderId}/can-transition-to/{targetStatus}")
    public ResponseEntity<?> canTransitionTo(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @PathVariable UUID orderId,
            @PathVariable OrderStatus targetStatus) {
        
        log.info("ORDER CONTROLLER: Received transition check request for order {} to status {}", orderId, targetStatus);
        
        // Validate user owns the order
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        
        try {
            // Check if user owns the order
            OrderResponseDTO existingOrder = orderService.getById(orderId);
            if (!existingOrder.getUserId().equals(userId)) {
                log.warn("ORDER CONTROLLER: User {} attempted to check transition for order {} owned by {}", 
                        userId, orderId, existingOrder.getUserId());
                return ResponseEntity.status(403).body(new ErrorResponse("You do not own this order!"));
            }
            
            boolean canTransition = orderService.canOrderTransitionTo(orderId, targetStatus);
            TransitionCheckResponse response = TransitionCheckResponse.builder()
                    .orderId(orderId)
                    .targetStatus(targetStatus)
                    .canTransition(canTransition)
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("ORDER CONTROLLER: Error checking transition for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Undoes the last command that supports undo operations.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @return the result of the undo operation
     */
    @PostMapping("/undo-last")
    public ResponseEntity<?> undoLastCommand(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {
        
        log.info("ORDER CONTROLLER: Received undo request");
        
        // Validate user authentication (basic check)
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        
        // COMMAND PATTERN: Use integrated OrderService undo method
        CommandResult result = orderService.undoLastCommand();
        
        if (result.isSuccess()) {
            log.info("ORDER CONTROLLER: Successfully undone last command for user: {}", userId);
            return ResponseEntity.ok(result);
        } else {
            log.warn("ORDER CONTROLLER: Failed to undo last command: {}", result.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(result.getMessage()));
        }
    }

    /**
     * Gets information about commands that can be undone.
     *
     * COMMAND PATTERN: Provides visibility into command history.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @return information about undoable commands
     */
    @GetMapping("/undo-info")
    public ResponseEntity<?> getUndoInfo(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {
        
        // Validate user authentication (basic check)
        String jwt = token.replace(CommonConstants.BEARER_PREFIX, CommonConstants.EMPTY_STRING);
        UUID userId = jwtService.extractUserId(jwt);
        
        // COMMAND PATTERN: Use integrated OrderService command history method
        String historySummary = orderService.getCommandHistorySummary();
        boolean hasUndoableCommands = orderService.hasUndoableCommands();
        
        // Parse the summary to extract details (simple approach)
        int undoableCount = hasUndoableCommands ? 1 : 0; // Simplified for demo
        String lastCommand = hasUndoableCommands ? "Available" : null;
        
        UndoInfoResponse response = UndoInfoResponse.builder()
                .undoableCommandCount(undoableCount)
                .lastUndoableCommand(lastCommand)
                .hasUndoableCommands(hasUndoableCommands)
                .historySummary(historySummary)
                .build();
        
        log.debug("ORDER CONTROLLER: Returning undo info for user: {} - {}", userId, historySummary);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Request DTO for order cancellation.
     */
    @Data
    public static class CancellationRequest {
        private String reason;
    }

    /**
     * Response DTO for available actions.
     */
    @Data
    @AllArgsConstructor
    public static class AvailableActionsResponse {
        private UUID orderId;
        private String availableActions;
    }

    /**
     * Response DTO for transition checks.
     */
    @Data
    @Builder
    public static class TransitionCheckResponse {
        private UUID orderId;
        private OrderStatus targetStatus;
        private boolean canTransition;
    }

    /**
     * Response DTO for error messages.
     */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
    }

    /**
     * Response DTO for undo information.
     */
    @Data
    @Builder
    public static class UndoInfoResponse {
        private int undoableCommandCount;
        private String lastUndoableCommand;
        private boolean hasUndoableCommands;
        private String historySummary;
    }
}
