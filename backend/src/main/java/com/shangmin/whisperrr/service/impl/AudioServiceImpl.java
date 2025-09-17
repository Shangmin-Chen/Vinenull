package com.shangmin.whisperrr.service.impl;

import com.shangmin.whisperrr.dto.*;
import com.shangmin.whisperrr.exception.FileValidationException;
import com.shangmin.whisperrr.exception.TranscriptionNotFoundException;
import com.shangmin.whisperrr.service.AudioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of AudioService for handling audio transcription operations
 */
@Service
public class AudioServiceImpl implements AudioService {
    
    private static final Logger logger = LoggerFactory.getLogger(AudioServiceImpl.class);
    
    // In-memory storage for demonstration - in production, use a database
    private final Map<String, TranscriptionJob> transcriptionJobs = new ConcurrentHashMap<>();
    private final AtomicLong jobIdCounter = new AtomicLong(1);
    
    // Supported file types and size limit
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("mp3", "wav", "m4a");
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB
    
    @Override
    public AudioUploadResponse uploadAudio(MultipartFile audioFile) {
        logger.info("Processing audio upload: {}", audioFile.getOriginalFilename());
        
        // Validate the file
        validateAudioFile(audioFile);
        
        // Generate job ID
        String jobId = "job_" + jobIdCounter.getAndIncrement();
        
        // Create transcription job
        TranscriptionJob job = new TranscriptionJob(jobId, audioFile.getOriginalFilename(), TranscriptionStatus.PENDING);
        transcriptionJobs.put(jobId, job);
        
        // Simulate asynchronous processing
        processTranscriptionAsync(job);
        
        logger.info("Audio upload processed successfully with job ID: {}", jobId);
        
        return new AudioUploadResponse(
            jobId,
            LocalDateTime.now(),
            "Audio file uploaded successfully and transcription job started"
        );
    }
    
    @Override
    public TranscriptionStatusResponse getTranscriptionStatus(String jobId) {
        logger.info("Getting status for job: {}", jobId);
        
        TranscriptionJob job = transcriptionJobs.get(jobId);
        if (job == null) {
            throw new TranscriptionNotFoundException("Transcription job not found: " + jobId);
        }
        
        return new TranscriptionStatusResponse(
            jobId,
            job.getStatus(),
            job.getUpdatedAt(),
            getStatusMessage(job.getStatus())
        );
    }
    
    @Override
    public TranscriptionResultResponse getTranscriptionResult(String jobId) {
        logger.info("Getting result for job: {}", jobId);
        
        TranscriptionJob job = transcriptionJobs.get(jobId);
        if (job == null) {
            throw new TranscriptionNotFoundException("Transcription job not found: " + jobId);
        }
        
        if (job.getStatus() != TranscriptionStatus.COMPLETED) {
            throw new TranscriptionNotFoundException("Transcription job is not completed yet: " + jobId);
        }
        
        return new TranscriptionResultResponse(
            jobId,
            job.getTranscriptionText(),
            job.getCompletedAt(),
            job.getStatus()
        );
    }
    
    @Override
    public void validateAudioFile(MultipartFile audioFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new FileValidationException("Audio file is required");
        }
        
        // Check file size
        if (audioFile.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File size exceeds maximum allowed size of 25MB");
        }
        
        // Check file extension
        String originalFilename = audioFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileValidationException("File must have a valid name");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new FileValidationException("Unsupported file type. Supported types: " + SUPPORTED_EXTENSIONS);
        }
        
        // Check content type
        String contentType = audioFile.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            throw new FileValidationException("File must be an audio file");
        }
        
        logger.debug("File validation passed for: {}", originalFilename);
    }
    
    private void processTranscriptionAsync(TranscriptionJob job) {
        // Simulate asynchronous processing
        new Thread(() -> {
            try {
                logger.info("Starting transcription processing for job: {}", job.getJobId());
                
                // Update status to processing
                job.setStatus(TranscriptionStatus.PROCESSING);
                job.setUpdatedAt(LocalDateTime.now());
                
                // Simulate processing time
                Thread.sleep(2000);
                
                // Simulate transcription result
                String transcriptionText = "This is a simulated transcription result for the audio file: " + job.getFileName();
                
                // Update job with result
                job.setStatus(TranscriptionStatus.COMPLETED);
                job.setTranscriptionText(transcriptionText);
                job.setCompletedAt(LocalDateTime.now());
                job.setUpdatedAt(LocalDateTime.now());
                
                logger.info("Transcription completed for job: {}", job.getJobId());
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                job.setStatus(TranscriptionStatus.FAILED);
                job.setUpdatedAt(LocalDateTime.now());
                logger.error("Transcription processing interrupted for job: {}", job.getJobId(), e);
            } catch (Exception e) {
                job.setStatus(TranscriptionStatus.FAILED);
                job.setUpdatedAt(LocalDateTime.now());
                logger.error("Transcription processing failed for job: {}", job.getJobId(), e);
            }
        }).start();
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex + 1);
    }
    
    private String getStatusMessage(TranscriptionStatus status) {
        return switch (status) {
            case PENDING -> "Transcription job is pending";
            case PROCESSING -> "Transcription is in progress";
            case COMPLETED -> "Transcription completed successfully";
            case FAILED -> "Transcription failed";
        };
    }
    
    /**
     * Internal class to represent a transcription job
     */
    private static class TranscriptionJob {
        private String jobId;
        private String fileName;
        private TranscriptionStatus status;
        private String transcriptionText;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime completedAt;
        
        public TranscriptionJob(String jobId, String fileName, TranscriptionStatus status) {
            this.jobId = jobId;
            this.fileName = fileName;
            this.status = status;
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
        
        // Getters and setters
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public TranscriptionStatus getStatus() { return status; }
        public void setStatus(TranscriptionStatus status) { this.status = status; }
        
        public String getTranscriptionText() { return transcriptionText; }
        public void setTranscriptionText(String transcriptionText) { this.transcriptionText = transcriptionText; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    }
}
