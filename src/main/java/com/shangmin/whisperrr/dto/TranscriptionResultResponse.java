package com.shangmin.whisperrr.dto;

import java.time.LocalDateTime;

/**
 * DTO for transcription result response
 */
public class TranscriptionResultResponse {
    
    private String jobId;
    private String transcriptionText;
    private LocalDateTime completedAt;
    private TranscriptionStatus status;
    
    public TranscriptionResultResponse() {}
    
    public TranscriptionResultResponse(String jobId, String transcriptionText, LocalDateTime completedAt, TranscriptionStatus status) {
        this.jobId = jobId;
        this.transcriptionText = transcriptionText;
        this.completedAt = completedAt;
        this.status = status;
    }
    
    public String getJobId() {
        return jobId;
    }
    
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    
    public String getTranscriptionText() {
        return transcriptionText;
    }
    
    public void setTranscriptionText(String transcriptionText) {
        this.transcriptionText = transcriptionText;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public TranscriptionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TranscriptionStatus status) {
        this.status = status;
    }
}
