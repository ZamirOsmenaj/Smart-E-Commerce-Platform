package com.example.ecommerce.controller;

import com.example.ecommerce.constants.CommonConstants;

import com.example.ecommerce.dto.response.PaymentResponseDTO;
import com.example.ecommerce.security.OwnershipValidationService;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
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
public class PaymentController {

    private final PaymentService paymentService;
    private final OwnershipValidationService ownershipValidationService;

    /**
     * Initiates a payment for the specific order.
     *
     * @param authHeader the authorization header containing the JWT token
     * @param orderId    the unique identifier of the order to be paid
     * @param provider   the payment provider to use (default: {@code mockPayment})
     * @return {@link PaymentResponseDTO} representing the payment outcome
     */
    @PostMapping("/{orderId}")
    public PaymentResponseDTO pay(
            @RequestHeader(CommonConstants.AUTH_HEADER) String authHeader,
            @PathVariable UUID orderId,
            @RequestParam(defaultValue = "mockPayment") String provider) {

        ownershipValidationService.validateOrderOwnership(authHeader, orderId);

        return paymentService.pay(orderId, provider);
    }
}
