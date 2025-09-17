package com.shangmin.whisperrr.controller;

import com.shangmin.whisperrr.dto.AudioUploadResponse;
import com.shangmin.whisperrr.dto.TranscriptionResultResponse;
import com.shangmin.whisperrr.dto.TranscriptionStatusResponse;
import com.shangmin.whisperrr.service.AudioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for audio transcription operations
 */
@RestController
@RequestMapping("/api/audio")
public class AudioController {
    
    private static final Logger logger = LoggerFactory.getLogger(AudioController.class);
    
    private final AudioService audioService;
    
    @Autowired
    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }
    
    /**
     * Upload an audio file for transcription
     * 
     * @param audioFile the audio file to upload
     * @return response containing job ID and status
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AudioUploadResponse> uploadAudio(
            @RequestParam("audioFile") @Valid MultipartFile audioFile) {
        
        logger.info("Received audio upload request for file: {}", audioFile.getOriginalFilename());
        
        try {
            AudioUploadResponse response = audioService.uploadAudio(audioFile);
            logger.info("Audio upload successful with job ID: {}", response.getJobId());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
            
        } catch (Exception e) {
            logger.error("Error processing audio upload: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Get the status of a transcription job
     * 
     * @param jobId the job ID
     * @return status response
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<TranscriptionStatusResponse> getTranscriptionStatus(@PathVariable String jobId) {
        
        logger.info("Getting transcription status for job: {}", jobId);
        
        try {
            TranscriptionStatusResponse response = audioService.getTranscriptionStatus(jobId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting transcription status: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Get the transcription result for a completed job
     * 
     * @param jobId the job ID
     * @return transcription result
     */
    @GetMapping("/result/{jobId}")
    public ResponseEntity<TranscriptionResultResponse> getTranscriptionResult(@PathVariable String jobId) {
        
        logger.info("Getting transcription result for job: {}", jobId);
        
        try {
            TranscriptionResultResponse response = audioService.getTranscriptionResult(jobId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting transcription result: {}", e.getMessage(), e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Health check endpoint
     * 
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Audio transcription service is running");
    }
}
