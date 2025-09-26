package com.example.ecommerce.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Command Invoker that executes commands and maintains command history.
 * Supports undo operations for commands that support it.
 */
@Component
@Slf4j
public class CommandInvoker {
    
    private final Stack<Command> commandHistory = new Stack<>();
    private final List<CommandResult> executionResults = new ArrayList<>();
    
    /**
     * Executes a command and stores it in history if successful and supports undo.
     * 
     * @param command the command to execute
     * @return the result of command execution
     */
    public CommandResult execute(Command command) {
        try {
            log.info("INVOKER: Executing command: {}", command.getDescription());
            
            CommandResult result = command.execute();
            
            // Store execution result
            executionResults.add(result);
            
            if (result.isSuccess()) {
                // Only store in history if command supports undo and was successful
                if (command.supportsUndo()) {
                    commandHistory.push(command);
                    log.debug("INVOKER: Command added to history (supports undo): {}", command.getDescription());
                } else {
                    log.debug("INVOKER: Command executed but not added to history (no undo support): {}", command.getDescription());
                }
            } else {
                log.warn("INVOKER: Command failed, not added to history: {} - Error: {}", 
                        command.getDescription(), result.getMessage());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("INVOKER: Exception during command execution: {} - Error: {}", 
                    command.getDescription(), e.getMessage());
            
            CommandResult errorResult = CommandResult.failure("Command execution failed: " + e.getMessage(), e);
            executionResults.add(errorResult);
            return errorResult;
        }
    }
    
    /**
     * Undoes the last command that supports undo operations.
     * 
     * @return the result of the undo operation
     */
    public CommandResult undoLast() {
        if (commandHistory.isEmpty()) {
            log.warn("INVOKER: No commands available to undo");
            return CommandResult.failure("No commands available to undo");
        }
        
        Command lastCommand = commandHistory.pop();
        
        try {
            log.info("INVOKER: Undoing last command: {}", lastCommand.getDescription());
            
            CommandResult result = lastCommand.undo();
            executionResults.add(result);
            
            if (result.isSuccess()) {
                log.info("INVOKER: Successfully undone command: {}", lastCommand.getDescription());
            } else {
                log.warn("INVOKER: Failed to undo command: {} - Error: {}", 
                        lastCommand.getDescription(), result.getMessage());
                // Put the command back in history since undo failed
                commandHistory.push(lastCommand);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("INVOKER: Exception during undo operation: {} - Error: {}", 
                    lastCommand.getDescription(), e.getMessage());
            
            // Put the command back in history since undo failed
            commandHistory.push(lastCommand);
            
            CommandResult errorResult = CommandResult.failure("Undo operation failed: " + e.getMessage(), e);
            executionResults.add(errorResult);
            return errorResult;
        }
    }
    
    /**
     * Returns the number of commands that can be undone.
     * 
     * @return count of undoable commands
     */
    public int getUndoableCommandCount() {
        return commandHistory.size();
    }
    
    /**
     * Returns a description of the last command that can be undone.
     * 
     * @return description of the last undoable command, or null if none
     */
    public String getLastUndoableCommandDescription() {
        if (commandHistory.isEmpty()) {
            return null;
        }
        return commandHistory.peek().getDescription();
    }
}