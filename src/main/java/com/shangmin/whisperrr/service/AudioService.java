package com.shangmin.whisperrr.service;

import com.shangmin.whisperrr.dto.AudioUploadResponse;
import com.shangmin.whisperrr.dto.TranscriptionResultResponse;
import com.shangmin.whisperrr.dto.TranscriptionStatusResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for audio transcription operations
 */
public interface AudioService {
    
    /**
     * Upload and process an audio file for transcription
     * @param audioFile the audio file to transcribe
     * @return response containing job ID and status
     */
    AudioUploadResponse uploadAudio(MultipartFile audioFile);
    
    /**
     * Get the status of a transcription job
     * @param jobId the job ID
     * @return status response
     */
    TranscriptionStatusResponse getTranscriptionStatus(String jobId);
    
    /**
     * Get the transcription result for a completed job
     * @param jobId the job ID
     * @return transcription result
     */
    TranscriptionResultResponse getTranscriptionResult(String jobId);
    
    /**
     * Validate an audio file
     * @param audioFile the file to validate
     * @throws com.shangmin.whisperrr.exception.FileValidationException if validation fails
     */
    void validateAudioFile(MultipartFile audioFile);
}
