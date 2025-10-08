package com.example.ecommerce.controller;

import com.example.ecommerce.constants.CommonConstants;
import com.example.ecommerce.constants.MessageConstants;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.PaymentResponseDTO;
import com.example.ecommerce.security.OwnershipValidationService;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller for handling payment-related operations.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final OwnershipValidationService ownershipValidationService;

    /**
     * Initiates a payment for the specific order.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param orderId    the unique identifier of the order to be paid
     * @param provider   the payment provider to use (default: {@code mockPayment})
     * @return a standardized API response containing the payment outcome
     */
    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponseDTO>> pay(
            @RequestHeader(CommonConstants.AUTH_HEADER) String authHeader,
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "mockPayment") String provider) {

        try {
            ownershipValidationService.validateOrderOwnership(authHeader, orderId);
            PaymentResponseDTO paymentResponse = paymentService.pay(orderId, provider);
            
            log.info("PAYMENT CONTROLLER: Payment processed successfully for order {} using provider {}", orderId, provider);
            return ResponseEntity.ok(ApiResponse.success(paymentResponse, MessageConstants.PAYMENT_PROCESSED_SUCCESS));
        } catch (Exception e) {
            log.error("PAYMENT CONTROLLER: Payment failed for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), MessageConstants.PAYMENT_FAILED_CODE));
        }
    }
}
