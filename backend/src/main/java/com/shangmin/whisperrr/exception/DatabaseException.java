package com.shangmin.whisperrr.exception;

/**
 * Base exception class for database-related errors.
 * 
 * @author shangmin
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {
    
    /**
     * Constructs a new database exception with the specified detail message.
     * 
     * @param message the detail message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new database exception with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new database exception with the specified cause.
     * 
     * @param cause the cause of the exception
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
