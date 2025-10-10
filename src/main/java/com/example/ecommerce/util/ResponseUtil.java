package com.example.ecommerce.util;

import com.example.ecommerce.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for creating standardized API responses.
 * Provides convenient methods for success and error responses.
 */
public final class ResponseUtil {

    private ResponseUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a successful ResponseEntity with data and message.
     *
     * @param data the response data
     * @param message the success message
     * @param <T> the type of data
     * @return ResponseEntity with success response
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Creates a successful ResponseEntity with data only.
     *
     * @param data the response data
     * @param <T> the type of data
     * @return ResponseEntity with success response
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Creates an error ResponseEntity with message and error code.
     *
     * @param errorMessage the error message
     * @param errorCode the error code
     * @param <T> the type of data
     * @return ResponseEntity with error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String errorMessage, String errorCode) {
        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage, errorCode));
    }

    /**
     * Creates an error ResponseEntity with message only.
     *
     * @param errorMessage the error message
     * @param <T> the type of data
     * @return ResponseEntity with error response
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String errorMessage) {
        return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
    }
}