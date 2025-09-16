package com.shangmin.whisperrr.dto;

import java.time.LocalDateTime;

/**
 * DTO for transcription status response
 */
public class TranscriptionStatusResponse {
    
    private String jobId;
    private TranscriptionStatus status;
    private LocalDateTime timestamp;
    private String message;
    
    public TranscriptionStatusResponse() {}
    
    public TranscriptionStatusResponse(String jobId, TranscriptionStatus status, LocalDateTime timestamp, String message) {
        this.jobId = jobId;
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public TranscriptionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TranscriptionStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
