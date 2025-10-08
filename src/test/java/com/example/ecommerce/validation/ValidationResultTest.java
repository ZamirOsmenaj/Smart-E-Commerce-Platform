package com.example.ecommerce.validation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationResult.
 * Testing validation result creation, error handling, and state management.
 */
class ValidationResultTest {

    @Test
    void shouldCreateSuccessfulValidationResult() {
        String step = "test-step";
        
        ValidationResult result = ValidationResult.success(step);
        
        assertTrue(result.isValid());
        assertEquals(step, result.getValidationStep());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    void shouldCreateFailureValidationResultWithSingleError() {
        String step = "validation-step";
        String error = "Validation failed";
        
        ValidationResult result = ValidationResult.failure(step, error);
        
        assertFalse(result.isValid());
        assertEquals(step, result.getValidationStep());
        assertEquals(1, result.getErrors().size());
        assertEquals(error, result.getErrors().get(0));
    }

    @Test
    void shouldCreateFailureValidationResultWithMultipleErrors() {
        String step = "multi-validation";
        List<String> errors = Arrays.asList("Error 1", "Error 2", "Error 3");
        
        ValidationResult result = ValidationResult.failure(step, errors);
        
        assertFalse(result.isValid());
        assertEquals(step, result.getValidationStep());
        assertEquals(3, result.getErrors().size());
        assertEquals(errors, result.getErrors());
    }

    @Test
    void shouldAddErrorToExistingResult() {
        ValidationResult result = ValidationResult.success("test");
        String newError = "New error added";
        
        result.addError(newError);
        
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals(newError, result.getErrors().get(0));
    }

    @Test
    void shouldAddMultipleErrorsSequentially() {
        ValidationResult result = ValidationResult.success("test");
        
        result.addError("First error");
        result.addError("Second error");
        result.addError("Third error");
        
        assertFalse(result.isValid());
        assertEquals(3, result.getErrors().size());
        assertEquals("First error", result.getErrors().get(0));
        assertEquals("Second error", result.getErrors().get(1));
        assertEquals("Third error", result.getErrors().get(2));
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        ValidationResult result = new ValidationResult();
        
        assertFalse(result.isValid()); // default boolean is false
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
        assertNull(result.getValidationStep());
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        boolean valid = true;
        List<String> errors = Arrays.asList("Test error");
        String step = "constructor-test";
        
        ValidationResult result = new ValidationResult(valid, errors, step);
        
        assertEquals(valid, result.isValid());
        assertEquals(errors, result.getErrors());
        assertEquals(step, result.getValidationStep());
    }

    @Test
    void shouldHandleNullErrorsInConstructor() {
        ValidationResult result = new ValidationResult(true, null, "test");
        
        assertTrue(result.isValid());
        assertNull(result.getErrors());
        assertEquals("test", result.getValidationStep());
    }

    @Test
    void shouldMaintainStateAfterAddingErrorToFailedResult() {
        ValidationResult result = ValidationResult.failure("test", "Initial error");
        
        result.addError("Additional error");
        
        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertEquals("Initial error", result.getErrors().get(0));
        assertEquals("Additional error", result.getErrors().get(1));
    }

    @Test
    void shouldHandleEmptyErrorsList() {
        ValidationResult result = ValidationResult.failure("test", List.of());
        
        assertFalse(result.isValid());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().isEmpty());
    }
}