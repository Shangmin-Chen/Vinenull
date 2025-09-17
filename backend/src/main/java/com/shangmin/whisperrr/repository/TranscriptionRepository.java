package com.shangmin.whisperrr.repository;

import com.shangmin.whisperrr.entity.Transcription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Transcription entity operations.
 * 
 * @author shangmin
 * @version 1.0
 */
@Repository
public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
    
    /**
     * Finds a transcription by job ID.
     * 
     * @param jobId the job ID
     * @return Optional<Transcription> the transcription if found
     */
    @Query("SELECT t FROM Transcription t WHERE t.job.jobId = :jobId")
    Optional<Transcription> findByJobJobId(@Param("jobId") UUID jobId);
    
    /**
     * Finds transcriptions by language.
     * 
     * @param language the language code
     * @return List<Transcription> list of transcriptions in the specified language
     */
    List<Transcription> findByLanguage(String language);
    
    /**
     * Finds transcriptions by language with pagination.
     * 
     * @param language the language code
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of transcriptions in the specified language
     */
    Page<Transcription> findByLanguage(String language, Pageable pageable);
    
    /**
     * Finds transcriptions with confidence greater than the specified value.
     * 
     * @param confidence the minimum confidence score
     * @return List<Transcription> list of transcriptions with high confidence
     */
    List<Transcription> findByConfidenceGreaterThan(Double confidence);
    
    /**
     * Finds transcriptions with confidence greater than the specified value with pagination.
     * 
     * @param confidence the minimum confidence score
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of transcriptions with high confidence
     */
    Page<Transcription> findByConfidenceGreaterThan(Double confidence, Pageable pageable);
    
    /**
     * Finds transcriptions with confidence less than the specified value.
     * 
     * @param confidence the maximum confidence score
     * @return List<Transcription> list of transcriptions with low confidence
     */
    List<Transcription> findByConfidenceLessThan(Double confidence);
    
    /**
     * Finds transcriptions with confidence between the specified values.
     * 
     * @param minConfidence the minimum confidence score
     * @param maxConfidence the maximum confidence score
     * @return List<Transcription> list of transcriptions with confidence in the range
     */
    List<Transcription> findByConfidenceBetween(Double minConfidence, Double maxConfidence);
    
    /**
     * Searches transcriptions by text containing the given search term.
     * 
     * @param searchText the text to search for
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of matching transcriptions
     */
    @Query("SELECT t FROM Transcription t WHERE LOWER(t.text) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Transcription> searchByTextContaining(@Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Performs full-text search on transcription text.
     * 
     * @param searchText the text to search for
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of matching transcriptions
     */
    @Query(value = "SELECT * FROM transcriptions WHERE to_tsvector('english', text) @@ plainto_tsquery('english', :searchText)", 
           nativeQuery = true)
    Page<Transcription> fullTextSearch(@Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Finds transcriptions created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return List<Transcription> list of transcriptions created in the date range
     */
    @Query("SELECT t FROM Transcription t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Transcription> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    /**
     * Finds transcriptions by language and confidence range.
     * 
     * @param language the language code
     * @param minConfidence the minimum confidence score
     * @param maxConfidence the maximum confidence score
     * @return List<Transcription> list of transcriptions matching the criteria
     */
    @Query("SELECT t FROM Transcription t WHERE t.language = :language AND t.confidence BETWEEN :minConfidence AND :maxConfidence")
    List<Transcription> findByLanguageAndConfidenceBetween(@Param("language") String language,
                                                          @Param("minConfidence") Double minConfidence,
                                                          @Param("maxConfidence") Double maxConfidence);
    
    /**
     * Finds transcriptions by language and confidence range with pagination.
     * 
     * @param language the language code
     * @param minConfidence the minimum confidence score
     * @param maxConfidence the maximum confidence score
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of transcriptions matching the criteria
     */
    @Query("SELECT t FROM Transcription t WHERE t.language = :language AND t.confidence BETWEEN :minConfidence AND :maxConfidence")
    Page<Transcription> findByLanguageAndConfidenceBetween(@Param("language") String language,
                                                          @Param("minConfidence") Double minConfidence,
                                                          @Param("maxConfidence") Double maxConfidence,
                                                          Pageable pageable);
    
    /**
     * Finds transcriptions with the highest confidence scores.
     * 
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of transcriptions with highest confidence
     */
    @Query("SELECT t FROM Transcription t WHERE t.confidence IS NOT NULL ORDER BY t.confidence DESC")
    Page<Transcription> findHighestConfidenceTranscriptions(Pageable pageable);
    
    /**
     * Finds transcriptions with the lowest confidence scores.
     * 
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of transcriptions with lowest confidence
     */
    @Query("SELECT t FROM Transcription t WHERE t.confidence IS NOT NULL ORDER BY t.confidence ASC")
    Page<Transcription> findLowestConfidenceTranscriptions(Pageable pageable);
    
    /**
     * Finds transcriptions by word count range.
     * 
     * @param minWords the minimum word count
     * @param maxWords the maximum word count
     * @return List<Transcription> list of transcriptions with word count in the range
     */
    @Query("SELECT t FROM Transcription t WHERE " +
           "LENGTH(t.text) - LENGTH(REPLACE(t.text, ' ', '')) + 1 BETWEEN :minWords AND :maxWords")
    List<Transcription> findByWordCountBetween(@Param("minWords") Integer minWords, 
                                              @Param("maxWords") Integer maxWords);
    
    /**
     * Finds transcriptions by character count range.
     * 
     * @param minChars the minimum character count
     * @param maxChars the maximum character count
     * @return List<Transcription> list of transcriptions with character count in the range
     */
    @Query("SELECT t FROM Transcription t WHERE LENGTH(t.text) BETWEEN :minChars AND :maxChars")
    List<Transcription> findByCharacterCountBetween(@Param("minChars") Integer minChars, 
                                                   @Param("maxChars") Integer maxChars);
    
    /**
     * Counts transcriptions by language.
     * 
     * @param language the language code
     * @return long count of transcriptions in the specified language
     */
    long countByLanguage(String language);
    
    /**
     * Counts transcriptions with confidence greater than the specified value.
     * 
     * @param confidence the minimum confidence score
     * @return long count of transcriptions with high confidence
     */
    long countByConfidenceGreaterThan(Double confidence);
    
    /**
     * Counts transcriptions with confidence less than the specified value.
     * 
     * @param confidence the maximum confidence score
     * @return long count of transcriptions with low confidence
     */
    long countByConfidenceLessThan(Double confidence);
    
    /**
     * Calculates average confidence by language.
     * 
     * @param language the language code
     * @return Double average confidence score
     */
    @Query("SELECT AVG(t.confidence) FROM Transcription t WHERE t.language = :language AND t.confidence IS NOT NULL")
    Double getAverageConfidenceByLanguage(@Param("language") String language);
    
    /**
     * Calculates overall average confidence.
     * 
     * @return Double overall average confidence score
     */
    @Query("SELECT AVG(t.confidence) FROM Transcription t WHERE t.confidence IS NOT NULL")
    Double getOverallAverageConfidence();
    
    /**
     * Finds transcriptions with segments JSON data.
     * 
     * @return List<Transcription> list of transcriptions with segments data
     */
    @Query("SELECT t FROM Transcription t WHERE t.segmentsJson IS NOT NULL AND t.segmentsJson != ''")
    List<Transcription> findTranscriptionsWithSegments();
    
    /**
     * Finds transcriptions without segments JSON data.
     * 
     * @return List<Transcription> list of transcriptions without segments data
     */
    @Query("SELECT t FROM Transcription t WHERE t.segmentsJson IS NULL OR t.segmentsJson = ''")
    List<Transcription> findTranscriptionsWithoutSegments();
    
    /**
     * Finds transcriptions by duration range.
     * 
     * @param minDuration the minimum duration in seconds
     * @param maxDuration the maximum duration in seconds
     * @return List<Transcription> list of transcriptions with duration in the range
     */
    @Query("SELECT t FROM Transcription t WHERE t.duration BETWEEN :minDuration AND :maxDuration")
    List<Transcription> findByDurationBetween(@Param("minDuration") Double minDuration, 
                                             @Param("maxDuration") Double maxDuration);
    
    /**
     * Finds the longest transcriptions by duration.
     * 
     * @param pageable pagination and sorting information
     * @return Page<Transcription> page of longest transcriptions
     */
    @Query("SELECT t FROM Transcription t WHERE t.duration IS NOT NULL ORDER BY t.duration DESC")
    Page<Transcription> findLongestTranscriptions(Pageable pageable);
    
    /**
     * Finds transcriptions that need quality review (low confidence).
     * 
     * @param confidenceThreshold the confidence threshold
     * @return List<Transcription> list of transcriptions needing review
     */
    @Query("SELECT t FROM Transcription t WHERE t.confidence < :confidenceThreshold ORDER BY t.confidence ASC")
    List<Transcription> findTranscriptionsNeedingReview(@Param("confidenceThreshold") Double confidenceThreshold);
}
