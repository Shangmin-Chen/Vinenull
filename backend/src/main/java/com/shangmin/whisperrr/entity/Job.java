package com.shangmin.whisperrr.entity;

import com.shangmin.whisperrr.enums.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Job entity representing a transcription job.
 * 
 * @author shangmin
 * @version 1.0
 */
@Entity
@Table(name = "jobs",
       indexes = {
           @Index(name = "idx_jobs_job_id", columnList = "job_id"),
           @Index(name = "idx_jobs_status", columnList = "status"),
           @Index(name = "idx_jobs_requested_by", columnList = "requested_by"),
           @Index(name = "idx_jobs_created_at", columnList = "created_at"),
           @Index(name = "idx_jobs_queue", columnList = "status, priority DESC, created_at ASC")
       })
public class Job extends BaseEntity {
    
    @NotNull(message = "Job ID is required")
    @Column(name = "job_id", nullable = false, unique = true, updatable = false)
    private UUID jobId;
    
    @NotNull(message = "Job status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private JobStatus status = JobStatus.PENDING;
    
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "model_used", length = 50)
    private String modelUsed;
    
    @NotNull(message = "Requested by user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false, foreignKey = @ForeignKey(name = "fk_jobs_requested_by"))
    private User requestedBy;
    
    @NotNull(message = "Audio file is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_file_id", nullable = false, foreignKey = @ForeignKey(name = "fk_jobs_audio_file"))
    private AudioFile audioFile;
    
    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Transcription transcription;
    
    /**
     * Default constructor.
     */
    public Job() {
        this.jobId = UUID.randomUUID();
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param requestedBy the user who requested the job
     * @param audioFile the audio file to transcribe
     */
    public Job(User requestedBy, AudioFile audioFile) {
        this();
        this.requestedBy = requestedBy;
        this.audioFile = audioFile;
    }
    
    /**
     * Gets the unique job ID.
     * 
     * @return UUID unique job ID
     */
    public UUID getJobId() {
        return jobId;
    }
    
    /**
     * Sets the unique job ID.
     * Note: This should typically not be called manually as it's set in the constructor.
     * 
     * @param jobId the unique job ID
     */
    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }
    
    /**
     * Gets the job status.
     * 
     * @return JobStatus the job status
     */
    public JobStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the job status.
     * 
     * @param status the job status
     */
    public void setStatus(JobStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the job priority.
     * 
     * @return Integer job priority
     */
    public Integer getPriority() {
        return priority;
    }
    
    /**
     * Sets the job priority.
     * 
     * @param priority the job priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    /**
     * Gets the start time of the job.
     * 
     * @return LocalDateTime start time
     */
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    /**
     * Sets the start time of the job.
     * 
     * @param startedAt the start time
     */
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    /**
     * Gets the completion time of the job.
     * 
     * @return LocalDateTime completion time
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    /**
     * Sets the completion time of the job.
     * 
     * @param completedAt the completion time
     */
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    /**
     * Gets the error message if the job failed.
     * 
     * @return String error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Sets the error message if the job failed.
     * 
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets the processing time in milliseconds.
     * 
     * @return Long processing time in milliseconds
     */
    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    /**
     * Sets the processing time in milliseconds.
     * 
     * @param processingTimeMs the processing time in milliseconds
     */
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    /**
     * Gets the model used for transcription.
     * 
     * @return String model used
     */
    public String getModelUsed() {
        return modelUsed;
    }
    
    /**
     * Sets the model used for transcription.
     * 
     * @param modelUsed the model used
     */
    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
    
    /**
     * Gets the user who requested the job.
     * 
     * @return User the user who requested the job
     */
    public User getRequestedBy() {
        return requestedBy;
    }
    
    /**
     * Sets the user who requested the job.
     * 
     * @param requestedBy the user who requested the job
     */
    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    /**
     * Gets the audio file to transcribe.
     * 
     * @return AudioFile the audio file
     */
    public AudioFile getAudioFile() {
        return audioFile;
    }
    
    /**
     * Sets the audio file to transcribe.
     * 
     * @param audioFile the audio file
     */
    public void setAudioFile(AudioFile audioFile) {
        this.audioFile = audioFile;
    }
    
    /**
     * Gets the transcription result.
     * 
     * @return Transcription the transcription result
     */
    public Transcription getTranscription() {
        return transcription;
    }
    
    /**
     * Sets the transcription result.
     * 
     * @param transcription the transcription result
     */
    public void setTranscription(Transcription transcription) {
        this.transcription = transcription;
    }
    
    /**
     * Checks if the job is in a terminal state.
     * 
     * @return true if the job is completed, failed, or cancelled
     */
    public boolean isTerminal() {
        return status != null && status.isTerminal();
    }
    
    /**
     * Checks if the job is currently processing.
     * 
     * @return true if the job is pending or processing
     */
    public boolean isProcessing() {
        return status != null && status.isProcessing();
    }
    
    /**
     * Marks the job as started.
     */
    public void markAsStarted() {
        this.status = JobStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }
    
    /**
     * Marks the job as completed.
     * 
     * @param processingTimeMs the processing time in milliseconds
     */
    public void markAsCompleted(Long processingTimeMs) {
        this.status = JobStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.processingTimeMs = processingTimeMs;
        this.errorMessage = null;
    }
    
    /**
     * Marks the job as failed.
     * 
     * @param errorMessage the error message
     * @param processingTimeMs the processing time in milliseconds
     */
    public void markAsFailed(String errorMessage, Long processingTimeMs) {
        this.status = JobStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
        this.processingTimeMs = processingTimeMs;
    }
    
    /**
     * Marks the job as cancelled.
     */
    public void markAsCancelled() {
        this.status = JobStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Gets the formatted processing time.
     * 
     * @return String formatted processing time
     */
    public String getFormattedProcessingTime() {
        if (processingTimeMs == null) {
            return "Unknown";
        }
        
        long ms = processingTimeMs;
        if (ms < 1000) {
            return ms + " ms";
        } else if (ms < 60000) {
            return String.format("%.1f s", ms / 1000.0);
        } else {
            long minutes = ms / 60000;
            long seconds = (ms % 60000) / 1000;
            return String.format("%d m %d s", minutes, seconds);
        }
    }
    
    @Override
    public String toString() {
        return "Job{" +
                "id=" + getId() +
                ", jobId=" + jobId +
                ", status=" + status +
                ", priority=" + priority +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                ", errorMessage='" + errorMessage + '\'' +
                ", processingTimeMs=" + processingTimeMs +
                ", modelUsed='" + modelUsed + '\'' +
                ", requestedBy=" + (requestedBy != null ? requestedBy.getUsername() : null) +
                ", audioFile=" + (audioFile != null ? audioFile.getFilename() : null) +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
