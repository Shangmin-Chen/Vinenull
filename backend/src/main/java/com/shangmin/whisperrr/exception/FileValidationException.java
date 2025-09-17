package com.shangmin.whisperrr.exception;

/**
 * Exception thrown when file validation fails
 */
public class FileValidationException extends RuntimeException {
    
    public FileValidationException(String message) {
        super(message);
    }
    
    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
