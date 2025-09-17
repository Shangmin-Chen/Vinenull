package com.shangmin.whisperrr.exception;

/**
 * Exception thrown when transcription job is not found
 */
public class TranscriptionNotFoundException extends RuntimeException {
    
    public TranscriptionNotFoundException(String message) {
        super(message);
    }
    
    public TranscriptionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
