package com.example.ecommerce.command;

/**
 * Base command interface following the Command Pattern.
 * Encapsulates a request as an object, allowing for parameterization of clients
 * with different requests, queuing of requests, and logging of requests.
 */
public interface Command {
    
    /**
     * Executes the command.
     * 
     * @return the result of the command execution
     * @throws Exception if command execution fails
     */
    CommandResult execute() throws Exception;
    
    /**
     * Undoes the command if possible.
     * Not all commands support undo operations.
     * 
     * @return the result of the undo operation
     * @throws UnsupportedOperationException if undo is not supported
     * @throws Exception if undo operation fails
     */
    default CommandResult undo() throws Exception {
        throw new UnsupportedOperationException("Undo operation not supported for this command");
    }
    
    /**
     * Returns whether this command supports undo operations.
     * 
     * @return true if undo is supported, false otherwise
     */
    default boolean supportsUndo() {
        return false;
    }
    
    /**
     * Returns a description of what this command does.
     * Useful for logging and debugging.
     * 
     * @return command description
     */
    String getDescription();
}