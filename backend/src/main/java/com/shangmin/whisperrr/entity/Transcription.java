package com.shangmin.whisperrr.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Transcription entity representing the result of a transcription job.
 * 
 * @author shangmin
 * @version 1.0
 */
@Entity
@Table(name = "transcriptions",
       indexes = {
           @Index(name = "idx_transcriptions_job_id", columnList = "job_id"),
           @Index(name = "idx_transcriptions_language", columnList = "language"),
           @Index(name = "idx_transcriptions_confidence", columnList = "confidence")
       })
public class Transcription extends BaseEntity {
    
    @NotNull(message = "Transcription text is required")
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Size(max = 10, message = "Language code must not exceed 10 characters")
    @Column(name = "language", length = 10)
    private String language;
    
    @DecimalMin(value = "0.0", message = "Confidence must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Confidence must be at most 1.0")
    @Column(name = "confidence")
    private Double confidence;
    
    @Column(name = "duration")
    private Double duration;
    
    @Column(name = "segments_json", columnDefinition = "TEXT")
    private String segmentsJson;
    
    @NotNull(message = "Job is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_transcriptions_job"))
    private Job job;
    
    /**
     * Default constructor.
     */
    public Transcription() {
        // Default constructor for JPA
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param text the transcribed text
     * @param job the associated job
     */
    public Transcription(String text, Job job) {
        this.text = text;
        this.job = job;
    }
    
    /**
     * Gets the transcribed text.
     * 
     * @return String transcribed text
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the transcribed text.
     * 
     * @param text the transcribed text
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * Gets the detected language code.
     * 
     * @return String language code
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * Sets the detected language code.
     * 
     * @param language the language code
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    
    /**
     * Gets the confidence score (0.0 to 1.0).
     * 
     * @return Double confidence score
     */
    public Double getConfidence() {
        return confidence;
    }
    
    /**
     * Sets the confidence score (0.0 to 1.0).
     * 
     * @param confidence the confidence score
     */
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
    
    /**
     * Gets the duration of the transcription in seconds.
     * 
     * @return Double duration in seconds
     */
    public Double getDuration() {
        return duration;
    }
    
    /**
     * Sets the duration of the transcription in seconds.
     * 
     * @param duration the duration in seconds
     */
    public void setDuration(Double duration) {
        this.duration = duration;
    }
    
    /**
     * Gets the segments JSON data.
     * 
     * @return String segments JSON
     */
    public String getSegmentsJson() {
        return segmentsJson;
    }
    
    /**
     * Sets the segments JSON data.
     * 
     * @param segmentsJson the segments JSON
     */
    public void setSegmentsJson(String segmentsJson) {
        this.segmentsJson = segmentsJson;
    }
    
    /**
     * Gets the associated job.
     * 
     * @return Job the associated job
     */
    public Job getJob() {
        return job;
    }
    
    /**
     * Sets the associated job.
     * 
     * @param job the associated job
     */
    public void setJob(Job job) {
        this.job = job;
    }
    
    /**
     * Gets the word count of the transcription.
     * 
     * @return int word count
     */
    public int getWordCount() {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
    
    /**
     * Gets the character count of the transcription.
     * 
     * @return int character count
     */
    public int getCharacterCount() {
        return text != null ? text.length() : 0;
    }
    
    /**
     * Gets the confidence as a percentage.
     * 
     * @return String confidence percentage
     */
    public String getConfidencePercentage() {
        if (confidence == null) {
            return "Unknown";
        }
        return String.format("%.1f%%", confidence * 100);
    }
    
    /**
     * Gets the duration in a human-readable format.
     * 
     * @return String human-readable duration
     */
    public String getFormattedDuration() {
        if (duration == null) {
            return "Unknown";
        }
        
        long totalSeconds = duration.longValue();
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Gets a preview of the transcription text.
     * 
     * @param maxLength maximum length of the preview
     * @return String preview of the text
     */
    public String getTextPreview(int maxLength) {
        if (text == null) {
            return "";
        }
        
        if (text.length() <= maxLength) {
            return text;
        }
        
        return text.substring(0, maxLength) + "...";
    }
    
    /**
     * Checks if the transcription has high confidence.
     * 
     * @return true if confidence is above 0.8, false otherwise
     */
    public boolean isHighConfidence() {
        return confidence != null && confidence > 0.8;
    }
    
    /**
     * Checks if the transcription has medium confidence.
     * 
     * @return true if confidence is between 0.5 and 0.8, false otherwise
     */
    public boolean isMediumConfidence() {
        return confidence != null && confidence >= 0.5 && confidence <= 0.8;
    }
    
    /**
     * Checks if the transcription has low confidence.
     * 
     * @return true if confidence is below 0.5, false otherwise
     */
    public boolean isLowConfidence() {
        return confidence != null && confidence < 0.5;
    }
    
    @Override
    public String toString() {
        return "Transcription{" +
                "id=" + getId() +
                ", text='" + getTextPreview(50) + '\'' +
                ", language='" + language + '\'' +
                ", confidence=" + confidence +
                ", duration=" + duration +
                ", job=" + (job != null ? job.getJobId() : null) +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
