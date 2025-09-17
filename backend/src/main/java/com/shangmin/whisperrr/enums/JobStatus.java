package com.shangmin.whisperrr.enums;

/**
 * Enum representing the status of a transcription job.
 * 
 * @author shangmin
 * @version 1.0
 */
public enum JobStatus {
    PENDING("Pending processing"),
    PROCESSING("Currently processing"),
    COMPLETED("Successfully completed"),
    FAILED("Processing failed"),
    CANCELLED("Job cancelled");
    
    private final String description;
    
    /**
     * Constructor for JobStatus enum.
     * 
     * @param description Human-readable description of the status
     */
    JobStatus(String description) {
        this.description = description;
    }
    
    /**
     * Gets the description of the job status.
     * 
     * @return String description of the status
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if the job is in a terminal state (completed, failed, or cancelled).
     * 
     * @return true if the job is in a terminal state, false otherwise
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * Checks if the job is in a processing state (pending or processing).
     * 
     * @return true if the job is processing, false otherwise
     */
    public boolean isProcessing() {
        return this == PENDING || this == PROCESSING;
    }
}
