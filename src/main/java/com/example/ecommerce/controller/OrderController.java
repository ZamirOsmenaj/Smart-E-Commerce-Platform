package com.example.ecommerce.controller;

import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.dto.OrderResponseDTO;
import com.example.ecommerce.dto.request.CancellationRequestDTO;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.AvailableActionsResponseDTO;
import com.example.ecommerce.dto.response.TransitionCheckResponseDTO;
import com.example.ecommerce.dto.response.UndoInfoResponseDTO;
import com.example.ecommerce.security.OwnershipValidationService;
import com.example.ecommerce.service.OrderService;
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
    private final OwnershipValidationService ownershipValidationService;

    /**
     * Retrieves all orders for the authenticated user.
     *
     * @param token the JWT authorization header containing the Bearer token
     * @return a standardized API response containing the user's orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrders(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {

        UUID userId = ownershipValidationService.extractUserIdFromToken(token);
        List<OrderResponseDTO> orders = orderService.getOrdersByUser(userId);

        
        log.debug("ORDER CONTROLLER: Retrieved {} orders for user {}", orders.size(), userId);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
    }

    /**
     * Creates a new order for the authenticated user using Command Pattern.
     *
     * @param token the JWT authorization header containing the Bearer token
     * @param request the request containing order details
     * @return a standardized API response containing the created order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @RequestBody CreateOrderRequestDTO request) {

        UUID userId = ownershipValidationService.extractUserIdFromToken(token);
        
        // COMMAND PATTERN: Use integrated OrderService command method
        CommandResult result = orderService.createOrderWithCommand(userId, request);
        
        if (result.isSuccess()) {
            log.info("ORDER CONTROLLER: Order created successfully via command pattern");
            return ResponseEntity.ok(ApiResponse.success((OrderResponseDTO) result.getData(), "Order created successfully"));
        } else {
            log.error("ORDER CONTROLLER: Order creation failed: {}", result.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage(), "ORDER_CREATION_FAILED"));
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
     * @return a standardized API response containing the updated order
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @PathVariable UUID orderId,
            @RequestBody CancellationRequestDTO request) {
        
        log.info("ORDER CONTROLLER: Received cancel request for order {} with reason: {}", orderId, request.getReason());
        
        // Centralized ownership validation
        ownershipValidationService.validateOrderOwnership(token, orderId);
        
        // COMMAND PATTERN: Use integrated OrderService command method
        CommandResult result = orderService.cancelOrderWithCommand(orderId, request.getReason());
        
        if (result.isSuccess()) {
            log.info("ORDER CONTROLLER: Successfully cancelled order {} via command pattern", orderId);
            return ResponseEntity.ok(ApiResponse.success((OrderResponseDTO) result.getData(), "Order cancelled successfully"));
        } else {
            log.warn("ORDER CONTROLLER: Order cancellation failed: {}", result.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage(), "ORDER_CANCELLATION_FAILED"));
        }
    }

    /**
     * Gets available actions for an order in its current state.
     * 
     * STATE PATTERN: Shows what operations are allowed for the order.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @param orderId the ID of the order
     * @return a standardized API response containing available actions
     */
    @GetMapping("/{orderId}/available-actions")
    public ResponseEntity<ApiResponse<AvailableActionsResponseDTO>> getAvailableActions(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @PathVariable UUID orderId) {
        
        log.info("ORDER CONTROLLER: Received available-actions request for order {}", orderId);
        
        // Centralized ownership validation
        ownershipValidationService.validateOrderOwnership(token, orderId);
        
        String actions = orderService.getOrderAvailableActions(orderId);
        AvailableActionsResponseDTO response = new AvailableActionsResponseDTO(orderId, actions);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Available actions retrieved successfully"));
    }

    /**
     * Checks if an order can transition to a specific status.
     * 
     * STATE PATTERN: Validates state transitions.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @param orderId the ID of the order
     * @param targetStatus the target status to check
     * @return a standardized API response indicating whether the transition is allowed
     */
    @GetMapping("/{orderId}/can-transition-to/{targetStatus}")
    public ResponseEntity<ApiResponse<TransitionCheckResponseDTO>> canTransitionTo(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token,
            @PathVariable UUID orderId,
            @PathVariable OrderStatus targetStatus) {
        
        log.info("ORDER CONTROLLER: Received transition check request for order {} to status {}", orderId, targetStatus);
        
        // Centralized ownership validation
        ownershipValidationService.validateOrderOwnership(token, orderId);
        
        boolean canTransition = orderService.canOrderTransitionTo(orderId, targetStatus);
        TransitionCheckResponseDTO response = TransitionCheckResponseDTO.builder()
                .orderId(orderId)
                .targetStatus(targetStatus)
                .canTransition(canTransition)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Transition check completed successfully"));
    }

    /**
     * Undoes the last command that supports undo operations.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @return a standardized API response containing the result of the undo operation
     */
    @PostMapping("/undo-last")
    public ResponseEntity<ApiResponse<CommandResult>> undoLastCommand(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {
        
        log.info("ORDER CONTROLLER: Received undo request");
        
        // Validate user authentication (basic check)
        UUID userId = ownershipValidationService.extractUserIdFromToken(token);
        
        // COMMAND PATTERN: Use integrated OrderService undo method
        CommandResult result = orderService.undoLastCommand();
        
        if (result.isSuccess()) {
            log.info("ORDER CONTROLLER: Successfully undone last command for user: {}", userId);
            return ResponseEntity.ok(ApiResponse.success(result, "Command undone successfully"));
        } else {
            log.warn("ORDER CONTROLLER: Failed to undo last command: {}", result.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage(), "UNDO_FAILED"));
        }
    }

    /**
     * Gets information about commands that can be undone.
     *
     * COMMAND PATTERN: Provides visibility into command history.
     * 
     * @param token the JWT authorization header containing the Bearer token
     * @return a standardized API response containing information about undoable commands
     */
    @GetMapping("/undo-info")
    public ResponseEntity<ApiResponse<UndoInfoResponseDTO>> getUndoInfo(
            @RequestHeader(CommonConstants.AUTH_HEADER) String token) {
        
        // Validate user authentication (basic check)
        UUID userId = ownershipValidationService.extractUserIdFromToken(token);
        
        // COMMAND PATTERN: Use integrated OrderService command history method
        String historySummary = orderService.getCommandHistorySummary();
        boolean hasUndoableCommands = orderService.hasUndoableCommands();
        
        // Parse the summary to extract details (simple approach)
        int undoableCount = hasUndoableCommands ? 1 : 0; // Simplified for demo
        String lastCommand = hasUndoableCommands ? "Available" : null;
        
        UndoInfoResponseDTO response = UndoInfoResponseDTO.builder()
                .undoableCommandCount(undoableCount)
                .lastUndoableCommand(lastCommand)
                .hasUndoableCommands(hasUndoableCommands)
                .historySummary(historySummary)
                .build();
        
        log.debug("ORDER CONTROLLER: Returning undo info for user: {} - {}", userId, historySummary);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Undo information retrieved successfully"));
    }

}
