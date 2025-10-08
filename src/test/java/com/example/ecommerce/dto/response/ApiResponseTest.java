package com.example.ecommerce.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiResponse DTO.
 * Testing static factory methods, builder pattern, and response structure.
 */
class ApiResponseTest {

    @Test
    void shouldCreateSuccessResponseWithData() {
        String testData = "test data";
        
        ApiResponse<String> response = ApiResponse.success(testData);
        
        assertTrue(response.isSuccess());
        assertEquals(testData, response.getData());
        assertNull(response.getMessage());
        assertNull(response.getError());
        assertNull(response.getErrorCode());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateSuccessResponseWithDataAndMessage() {
        String testData = "test data";
        String message = "Operation successful";
        
        ApiResponse<String> response = ApiResponse.success(testData, message);
        
        assertTrue(response.isSuccess());
        assertEquals(testData, response.getData());
        assertEquals(message, response.getMessage());
        assertNull(response.getError());
        assertNull(response.getErrorCode());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateErrorResponseWithMessage() {
        String errorMessage = "Something went wrong";
        
        ApiResponse<String> response = ApiResponse.error(errorMessage);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getError());
        assertNull(response.getData());
        assertNull(response.getMessage());
        assertNull(response.getErrorCode());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateErrorResponseWithMessageAndCode() {
        String errorMessage = "Validation failed";
        String errorCode = "VALIDATION_ERROR";
        
        ApiResponse<String> response = ApiResponse.error(errorMessage, errorCode);
        
        assertFalse(response.isSuccess());
        assertEquals(errorMessage, response.getError());
        assertEquals(errorCode, response.getErrorCode());
        assertNull(response.getData());
        assertNull(response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void shouldCreateResponseWithBuilder() {
        String data = "builder data";
        String message = "builder message";
        LocalDateTime customTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(customTime)
                .build();
        
        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
        assertEquals(message, response.getMessage());
        assertEquals(customTime, response.getTimestamp());
    }

    @Test
    void shouldHaveDefaultTimestamp() {
        LocalDateTime before = LocalDateTime.now();
        ApiResponse<String> response = new ApiResponse<>();
        LocalDateTime after = LocalDateTime.now();
        
        assertNotNull(response.getTimestamp());
        assertTrue(response.getTimestamp().isAfter(before.minusSeconds(1)));
        assertTrue(response.getTimestamp().isBefore(after.plusSeconds(1)));
    }

    @Test
    void shouldHandleNullData() {
        ApiResponse<String> response = ApiResponse.success(null);
        
        assertTrue(response.isSuccess());
        assertNull(response.getData());
    }

    @Test
    void shouldHandleGenericTypes() {
        Integer intData = 42;
        ApiResponse<Integer> intResponse = ApiResponse.success(intData);
        
        assertTrue(intResponse.isSuccess());
        assertEquals(intData, intResponse.getData());
        assertEquals(Integer.class, intResponse.getData().getClass());
    }
}