package com.example.ecommerce.payment;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST controller that simulates payment processing for testing purposes.
 *
 * <p>
 * Provides a mock endpoint under <code>/api/mock-payments</code> which evaluates
 * payment requests based on a simple rule:
 * <ul>
 *   <li>Payments with amount &lt; 100.00 are {@code APPROVED}</li>
 *   <li>Payments with amount ≥ 100.00 are {@code DECLINED}</li>
 * </ul>
 * This controller is intended for local development, testing, or demo scenarios
 * where no real payment gateway is available.
 */
@RestController
@RequestMapping("/api/mock-payments")
public class MockPaymentController {

    /**
     * Processes a mock payment request and returns a simulated result.
     *
     * @param request the payment request containing the order ID and amount
     * @return a response indicating whether the payment was approved or declined
     */
    @PostMapping("/process")
    public ResponseEntity<ProcessPaymentResponse> process(@RequestBody ProcessPaymentRequest request) {
        // Very simple mock logic
        String result = request.getAmount().compareTo(BigDecimal.valueOf(100)) < 0 ? "APPROVED" : "DECLINED";
        return ResponseEntity.ok(new ProcessPaymentResponse(result));
    }

    /**
     * Represents a request to process a mock payment.
     */
    @Data
    public static class ProcessPaymentRequest {

        /**
         * The unique identifier of the order being paid for.
         */
        private String orderId;

        /**
         * The amount to be charged for the payment.
         */
        private BigDecimal amount;
    }

    /**
     * Represents the response returned after processing a mock payment.
     */
    @Data
    public static class ProcessPaymentResponse {

        /**
         * The status of the payment, either {@code APPROVED} or {@code DECLINED}.
         */
        private final String status;
    }

}
