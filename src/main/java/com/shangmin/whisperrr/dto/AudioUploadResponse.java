package com.shangmin.whisperrr.dto;

import java.time.LocalDateTime;

/**
 * DTO for audio upload response
 */
public class AudioUploadResponse {
    
    private String jobId;
    private LocalDateTime timestamp;
    private String message;
    
    public AudioUploadResponse() {}
    
    public AudioUploadResponse(String jobId, LocalDateTime timestamp, String message) {
        this.jobId = jobId;
        this.timestamp = timestamp;
        this.message = message;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
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
