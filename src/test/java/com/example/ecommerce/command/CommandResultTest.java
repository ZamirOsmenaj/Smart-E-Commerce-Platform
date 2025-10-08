package com.example.ecommerce.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CommandResult.
 * Testing command result creation, factory methods, and data handling.
 */
class CommandResultTest {

    @Test
    void shouldCreateSuccessfulResultWithData() {
        String testData = "test data";
        
        CommandResult result = CommandResult.success(testData);
        
        assertTrue(result.isSuccess());
        assertEquals("Command executed successfully", result.getMessage());
        assertEquals(testData, result.getData());
        assertNull(result.getError());
    }

    @Test
    void shouldCreateSuccessfulResultWithCustomMessage() {
        String message = "Custom success message";
        Integer data = 42;
        
        CommandResult result = CommandResult.success(message, data);
        
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertNull(result.getError());
    }

    @Test
    void shouldCreateFailureResultWithMessage() {
        String errorMessage = "Command failed";
        
        CommandResult result = CommandResult.failure(errorMessage);
        
        assertFalse(result.isSuccess());
        assertEquals(errorMessage, result.getMessage());
        assertNull(result.getData());
        assertNull(result.getError());
    }

    @Test
    void shouldCreateFailureResultWithException() {
        String errorMessage = "Command failed with exception";
        Exception exception = new RuntimeException("Test exception");
        
        CommandResult result = CommandResult.failure(errorMessage, exception);
        
        assertFalse(result.isSuccess());
        assertEquals(errorMessage, result.getMessage());
        assertEquals(exception, result.getError());
        assertNull(result.getData());
    }

    @Test
    void shouldCreateWithBuilder() {
        String message = "Builder test";
        String data = "builder data";
        Exception error = new IllegalArgumentException("Builder error");
        
        CommandResult result = CommandResult.builder()
                .success(false)
                .message(message)
                .data(data)
                .error(error)
                .build();
        
        assertFalse(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(error, result.getError());
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        CommandResult result = new CommandResult();
        
        assertFalse(result.isSuccess()); // default boolean is false
        assertNull(result.getMessage());
        assertNull(result.getData());
        assertNull(result.getError());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        boolean success = true;
        String message = "All args test";
        Object data = "test data";
        Exception error = new Exception("test error");
        
        CommandResult result = new CommandResult(success, message, data, error);
        
        assertEquals(success, result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
        assertEquals(error, result.getError());
    }

    @Test
    void shouldHandleNullData() {
        CommandResult result = CommandResult.success(null);
        
        assertTrue(result.isSuccess());
        assertNull(result.getData());
        assertEquals("Command executed successfully", result.getMessage());
    }

    @Test
    void shouldHandleComplexDataTypes() {
        Object complexData = new Object() {
            private final String value = "complex";
            public String getValue() { return value; }
        };
        
        CommandResult result = CommandResult.success("Complex data test", complexData);
        
        assertTrue(result.isSuccess());
        assertEquals(complexData, result.getData());
        assertEquals("Complex data test", result.getMessage());
    }

    @Test
    void shouldAllowModificationAfterCreation() {
        CommandResult result = CommandResult.success("initial");
        
        result.setSuccess(false);
        result.setMessage("modified message");
        result.setData("modified data");
        result.setError(new RuntimeException("modified error"));
        
        assertFalse(result.isSuccess());
        assertEquals("modified message", result.getMessage());
        assertEquals("modified data", result.getData());
        assertNotNull(result.getError());
        assertEquals("modified error", result.getError().getMessage());
    }
}