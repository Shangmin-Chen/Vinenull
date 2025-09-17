package com.shangmin.whisperrr.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for audio file upload request
 */
public class AudioUploadRequest {
    
    @NotNull(message = "Audio file is required")
    private MultipartFile audioFile;
    
    public AudioUploadRequest() {}
    
    public AudioUploadRequest(MultipartFile audioFile) {
        this.audioFile = audioFile;
    }
    
    public MultipartFile getAudioFile() {
        return audioFile;
    }
    
    public void setAudioFile(MultipartFile audioFile) {
        this.audioFile = audioFile;
    }
}
