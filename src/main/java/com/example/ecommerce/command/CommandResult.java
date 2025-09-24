package com.example.ecommerce.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the result of a command execution.
 * Contains success status, result data, and any error information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandResult {
    
    private boolean success;
    private String message;
    private Object data;
    private Exception error;
    
    /**
     * Creates a successful command result with data.
     */
    public static CommandResult success(Object data) {
        return CommandResult.builder()
                .success(true)
                .data(data)
                .message("Command executed successfully")
                .build();
    }
    
    /**
     * Creates a successful command result with custom message.
     */
    public static CommandResult success(String message, Object data) {
        return CommandResult.builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    /**
     * Creates a failed command result with error message.
     */
    public static CommandResult failure(String message) {
        return CommandResult.builder()
                .success(false)
                .message(message)
                .build();
    }
    
    /**
     * Creates a failed command result with exception.
     */
    public static CommandResult failure(String message, Exception error) {
        return CommandResult.builder()
                .success(false)
                .message(message)
                .error(error)
                .build();
    }
}