package com.shangmin.whisperrr.repository;

import com.shangmin.whisperrr.entity.AudioFile;
import com.shangmin.whisperrr.entity.User;
import com.shangmin.whisperrr.enums.AudioFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for AudioFile entity operations.
 * 
 * @author shangmin
 * @version 1.0
 */
@Repository
public interface AudioFileRepository extends JpaRepository<AudioFile, Long> {
    
    /**
     * Finds audio files by the user who uploaded them.
     * 
     * @param uploadedBy the user who uploaded the files
     * @return List<AudioFile> list of audio files uploaded by the user
     */
    List<AudioFile> findByUploadedBy(User uploadedBy);
    
    /**
     * Finds audio files by the user who uploaded them with pagination.
     * 
     * @param uploadedBy the user who uploaded the files
     * @param pageable pagination and sorting information
     * @return Page<AudioFile> page of audio files uploaded by the user
     */
    Page<AudioFile> findByUploadedBy(User uploadedBy, Pageable pageable);
    
    /**
     * Finds audio files by S3 key.
     * 
     * @param s3Key the S3 key
     * @return Optional<AudioFile> the audio file if found
     */
    Optional<AudioFile> findByS3Key(String s3Key);
    
    /**
     * Finds audio files by format and uploaded by user.
     * 
     * @param format the audio format
     * @param uploadedBy the user who uploaded the files
     * @return List<AudioFile> list of audio files matching the criteria
     */
    List<AudioFile> findByFormatAndUploadedBy(AudioFormat format, User uploadedBy);
    
    /**
     * Finds audio files by uploaded by user ordered by creation date descending.
     * 
     * @param uploadedBy the user who uploaded the files
     * @return List<AudioFile> list of audio files ordered by creation date
     */
    List<AudioFile> findByUploadedByOrderByCreatedAtDesc(User uploadedBy);
    
    /**
     * Finds audio files by format.
     * 
     * @param format the audio format
     * @return List<AudioFile> list of audio files with the specified format
     */
    List<AudioFile> findByFormat(AudioFormat format);
    
    /**
     * Finds audio files by format with pagination.
     * 
     * @param format the audio format
     * @param pageable pagination and sorting information
     * @return Page<AudioFile> page of audio files with the specified format
     */
    Page<AudioFile> findByFormat(AudioFormat format, Pageable pageable);
    
    /**
     * Finds audio files by checksum.
     * 
     * @param checksum the MD5 checksum
     * @return List<AudioFile> list of audio files with the specified checksum
     */
    List<AudioFile> findByChecksum(String checksum);
    
    /**
     * Finds audio files created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return List<AudioFile> list of audio files created in the date range
     */
    @Query("SELECT af FROM AudioFile af WHERE af.createdAt BETWEEN :startDate AND :endDate")
    List<AudioFile> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    /**
     * Finds audio files by format and date range.
     * 
     * @param format the audio format
     * @param startDate the start date
     * @param endDate the end date
     * @return List<AudioFile> list of audio files matching the criteria
     */
    @Query("SELECT af FROM AudioFile af WHERE af.format = :format AND af.createdAt BETWEEN :startDate AND :endDate")
    List<AudioFile> findByFormatAndCreatedAtBetween(@Param("format") AudioFormat format,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Finds audio files by user and date range.
     * 
     * @param uploadedBy the user who uploaded the files
     * @param startDate the start date
     * @param endDate the end date
     * @return List<AudioFile> list of audio files matching the criteria
     */
    @Query("SELECT af FROM AudioFile af WHERE af.uploadedBy = :uploadedBy AND af.createdAt BETWEEN :startDate AND :endDate")
    List<AudioFile> findByUploadedByAndCreatedAtBetween(@Param("uploadedBy") User uploadedBy,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Searches audio files by filename containing the given text.
     * 
     * @param searchText the text to search for
     * @param pageable pagination and sorting information
     * @return Page<AudioFile> page of matching audio files
     */
    @Query("SELECT af FROM AudioFile af WHERE " +
           "LOWER(af.filename) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(af.originalFilename) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<AudioFile> searchByFilename(@Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Counts audio files by uploaded by user.
     * 
     * @param uploadedBy the user who uploaded the files
     * @return long count of audio files uploaded by the user
     */
    long countByUploadedBy(User uploadedBy);
    
    /**
     * Counts audio files by format.
     * 
     * @param format the audio format
     * @return long count of audio files with the specified format
     */
    long countByFormat(AudioFormat format);
    
    /**
     * Counts audio files by format and uploaded by user.
     * 
     * @param format the audio format
     * @param uploadedBy the user who uploaded the files
     * @return long count of audio files matching the criteria
     */
    long countByFormatAndUploadedBy(AudioFormat format, User uploadedBy);
    
    /**
     * Sums the total file size by uploaded by user.
     * 
     * @param uploadedBy the user who uploaded the files
     * @return Long total file size in bytes
     */
    @Query("SELECT SUM(af.fileSize) FROM AudioFile af WHERE af.uploadedBy = :uploadedBy")
    Long sumFileSizeByUploadedBy(@Param("uploadedBy") User uploadedBy);
    
    /**
     * Sums the total file size by format.
     * 
     * @param format the audio format
     * @return Long total file size in bytes
     */
    @Query("SELECT SUM(af.fileSize) FROM AudioFile af WHERE af.format = :format")
    Long sumFileSizeByFormat(@Param("format") AudioFormat format);
    
    /**
     * Finds the largest audio files.
     * 
     * @param pageable pagination and sorting information
     * @return Page<AudioFile> page of largest audio files
     */
    @Query("SELECT af FROM AudioFile af ORDER BY af.fileSize DESC")
    Page<AudioFile> findLargestFiles(Pageable pageable);
    
    /**
     * Finds the longest audio files by duration.
     * 
     * @param pageable pagination and sorting information
     * @return Page<AudioFile> page of longest audio files
     */
    @Query("SELECT af FROM AudioFile af WHERE af.duration IS NOT NULL ORDER BY af.duration DESC")
    Page<AudioFile> findLongestFiles(Pageable pageable);
    
    /**
     * Finds audio files that have been processed (have associated jobs).
     * 
     * @return List<AudioFile> list of audio files that have been processed
     */
    @Query("SELECT DISTINCT af FROM AudioFile af JOIN af.jobs j WHERE j IS NOT NULL")
    List<AudioFile> findProcessedFiles();
    
    /**
     * Finds audio files that have not been processed (no associated jobs).
     * 
     * @return List<AudioFile> list of audio files that have not been processed
     */
    @Query("SELECT af FROM AudioFile af LEFT JOIN af.jobs j WHERE j IS NULL")
    List<AudioFile> findUnprocessedFiles();
    
    /**
     * Finds audio files with duplicate checksums.
     * 
     * @return List<AudioFile> list of audio files with duplicate checksums
     */
    @Query("SELECT af FROM AudioFile af WHERE af.checksum IN " +
           "(SELECT af2.checksum FROM AudioFile af2 WHERE af2.checksum IS NOT NULL " +
           "GROUP BY af2.checksum HAVING COUNT(af2.checksum) > 1)")
    List<AudioFile> findDuplicateFiles();
}
