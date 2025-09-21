package com.example.ecommerce.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of validation operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    
    private boolean valid;
    private List<String> errors = new ArrayList<>();
    private String validationStep;
    
    public static ValidationResult success(String step) {
        return new ValidationResult(true, new ArrayList<>(), step);
    }
    
    public static ValidationResult failure(String step, String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, errors, step);
    }
    
    public static ValidationResult failure(String step, List<String> errors) {
        return new ValidationResult(false, errors, step);
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }
}