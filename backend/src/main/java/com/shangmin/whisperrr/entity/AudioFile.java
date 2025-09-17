package com.shangmin.whisperrr.entity;

import com.shangmin.whisperrr.enums.AudioFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * AudioFile entity representing an uploaded audio file.
 * 
 * @author shangmin
 * @version 1.0
 */
@Entity
@Table(name = "audio_files",
       indexes = {
           @Index(name = "idx_audio_files_uploaded_by", columnList = "uploaded_by"),
           @Index(name = "idx_audio_files_s3_key", columnList = "s3_key"),
           @Index(name = "idx_audio_files_format", columnList = "format"),
           @Index(name = "idx_audio_files_created_at", columnList = "created_at")
       })
public class AudioFile extends BaseEntity {
    
    @NotBlank(message = "Filename is required")
    @Size(max = 255, message = "Filename must not exceed 255 characters")
    @Column(name = "filename", nullable = false, length = 255)
    private String filename;
    
    @NotBlank(message = "Original filename is required")
    @Size(max = 255, message = "Original filename must not exceed 255 characters")
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;
    
    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "duration")
    private Double duration;
    
    @NotNull(message = "Audio format is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 10)
    private AudioFormat format;
    
    @Size(max = 500, message = "S3 key must not exceed 500 characters")
    @Column(name = "s3_key", length = 500)
    private String s3Key;
    
    @Size(max = 32, message = "Checksum must not exceed 32 characters")
    @Column(name = "checksum", length = 32)
    private String checksum;
    
    @NotNull(message = "Uploaded by user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false, foreignKey = @ForeignKey(name = "fk_audio_files_uploaded_by"))
    private User uploadedBy;
    
    @OneToMany(mappedBy = "audioFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();
    
    /**
     * Default constructor.
     */
    public AudioFile() {
        // Default constructor for JPA
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param filename the filename
     * @param originalFilename the original filename
     * @param fileSize the file size in bytes
     * @param format the audio format
     * @param uploadedBy the user who uploaded the file
     */
    public AudioFile(String filename, String originalFilename, Long fileSize, AudioFormat format, User uploadedBy) {
        this.filename = filename;
        this.originalFilename = originalFilename;
        this.fileSize = fileSize;
        this.format = format;
        this.uploadedBy = uploadedBy;
    }
    
    /**
     * Gets the filename.
     * 
     * @return String filename
     */
    public String getFilename() {
        return filename;
    }
    
    /**
     * Sets the filename.
     * 
     * @param filename the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    /**
     * Gets the original filename.
     * 
     * @return String original filename
     */
    public String getOriginalFilename() {
        return originalFilename;
    }
    
    /**
     * Sets the original filename.
     * 
     * @param originalFilename the original filename
     */
    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
    
    /**
     * Gets the file size in bytes.
     * 
     * @return Long file size in bytes
     */
    public Long getFileSize() {
        return fileSize;
    }
    
    /**
     * Sets the file size in bytes.
     * 
     * @param fileSize the file size in bytes
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    /**
     * Gets the duration in seconds.
     * 
     * @return Double duration in seconds
     */
    public Double getDuration() {
        return duration;
    }
    
    /**
     * Sets the duration in seconds.
     * 
     * @param duration the duration in seconds
     */
    public void setDuration(Double duration) {
        this.duration = duration;
    }
    
    /**
     * Gets the audio format.
     * 
     * @return AudioFormat the audio format
     */
    public AudioFormat getFormat() {
        return format;
    }
    
    /**
     * Sets the audio format.
     * 
     * @param format the audio format
     */
    public void setFormat(AudioFormat format) {
        this.format = format;
    }
    
    /**
     * Gets the S3 key for cloud storage.
     * 
     * @return String S3 key
     */
    public String getS3Key() {
        return s3Key;
    }
    
    /**
     * Sets the S3 key for cloud storage.
     * 
     * @param s3Key the S3 key
     */
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }
    
    /**
     * Gets the MD5 checksum of the file.
     * 
     * @return String MD5 checksum
     */
    public String getChecksum() {
        return checksum;
    }
    
    /**
     * Sets the MD5 checksum of the file.
     * 
     * @param checksum the MD5 checksum
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    /**
     * Gets the user who uploaded the file.
     * 
     * @return User the user who uploaded the file
     */
    public User getUploadedBy() {
        return uploadedBy;
    }
    
    /**
     * Sets the user who uploaded the file.
     * 
     * @param uploadedBy the user who uploaded the file
     */
    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    
    /**
     * Gets the list of jobs associated with this audio file.
     * 
     * @return List<Job> list of jobs
     */
    public List<Job> getJobs() {
        return jobs;
    }
    
    /**
     * Sets the list of jobs associated with this audio file.
     * 
     * @param jobs the list of jobs
     */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
    
    /**
     * Gets the file size in a human-readable format.
     * 
     * @return String human-readable file size
     */
    public String getFormattedFileSize() {
        if (fileSize == null) {
            return "Unknown";
        }
        
        long bytes = fileSize;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        
        while (bytes >= 1024 && unitIndex < units.length - 1) {
            bytes /= 1024;
            unitIndex++;
        }
        
        return bytes + " " + units[unitIndex];
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
    
    @Override
    public String toString() {
        return "AudioFile{" +
                "id=" + getId() +
                ", filename='" + filename + '\'' +
                ", originalFilename='" + originalFilename + '\'' +
                ", fileSize=" + fileSize +
                ", duration=" + duration +
                ", format=" + format +
                ", s3Key='" + s3Key + '\'' +
                ", checksum='" + checksum + '\'' +
                ", uploadedBy=" + (uploadedBy != null ? uploadedBy.getUsername() : null) +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
