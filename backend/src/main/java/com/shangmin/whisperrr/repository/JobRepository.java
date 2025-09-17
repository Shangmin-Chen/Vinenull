package com.shangmin.whisperrr.repository;

import com.shangmin.whisperrr.entity.Job;
import com.shangmin.whisperrr.entity.User;
import com.shangmin.whisperrr.enums.JobStatus;
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
 * Repository interface for Job entity operations.
 * 
 * @author shangmin
 * @version 1.0
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    /**
     * Finds a job by its unique job ID.
     * 
     * @param jobId the unique job ID
     * @return Optional<Job> the job if found
     */
    Optional<Job> findByJobId(UUID jobId);
    
    /**
     * Finds jobs by status.
     * 
     * @param status the job status
     * @return List<Job> list of jobs with the specified status
     */
    List<Job> findByStatus(JobStatus status);
    
    /**
     * Finds jobs by status with pagination.
     * 
     * @param status the job status
     * @param pageable pagination and sorting information
     * @return Page<Job> page of jobs with the specified status
     */
    Page<Job> findByStatus(JobStatus status, Pageable pageable);
    
    /**
     * Finds jobs by requested by user and status.
     * 
     * @param requestedBy the user who requested the jobs
     * @param status the job status
     * @return List<Job> list of jobs matching the criteria
     */
    List<Job> findByRequestedByAndStatus(User requestedBy, JobStatus status);
    
    /**
     * Finds jobs by requested by user and status with pagination.
     * 
     * @param requestedBy the user who requested the jobs
     * @param status the job status
     * @param pageable pagination and sorting information
     * @return Page<Job> page of jobs matching the criteria
     */
    Page<Job> findByRequestedByAndStatus(User requestedBy, JobStatus status, Pageable pageable);
    
    /**
     * Finds pending jobs ordered by priority and creation time.
     * 
     * @return List<Job> list of pending jobs
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'PENDING' ORDER BY j.priority DESC, j.createdAt ASC")
    List<Job> findPendingJobs();
    
    /**
     * Finds pending jobs with pagination.
     * 
     * @param pageable pagination and sorting information
     * @return Page<Job> page of pending jobs
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'PENDING' ORDER BY j.priority DESC, j.createdAt ASC")
    Page<Job> findPendingJobs(Pageable pageable);
    
    /**
     * Finds jobs by requested by user ordered by creation date descending.
     * 
     * @param requestedBy the user who requested the jobs
     * @return List<Job> list of jobs ordered by creation date
     */
    List<Job> findByRequestedByOrderByCreatedAtDesc(User requestedBy);
    
    /**
     * Finds jobs by requested by user with pagination.
     * 
     * @param requestedBy the user who requested the jobs
     * @param pageable pagination and sorting information
     * @return Page<Job> page of jobs requested by the user
     */
    Page<Job> findByRequestedBy(User requestedBy, Pageable pageable);
    
    /**
     * Finds jobs by model used.
     * 
     * @param modelUsed the model used for transcription
     * @return List<Job> list of jobs using the specified model
     */
    List<Job> findByModelUsed(String modelUsed);
    
    /**
     * Finds jobs by model used with pagination.
     * 
     * @param modelUsed the model used for transcription
     * @param pageable pagination and sorting information
     * @return Page<Job> page of jobs using the specified model
     */
    Page<Job> findByModelUsed(String modelUsed, Pageable pageable);
    
    /**
     * Finds jobs created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return List<Job> list of jobs created in the date range
     */
    @Query("SELECT j FROM Job j WHERE j.createdAt BETWEEN :startDate AND :endDate")
    List<Job> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Finds jobs by status and date range.
     * 
     * @param status the job status
     * @param startDate the start date
     * @param endDate the end date
     * @return List<Job> list of jobs matching the criteria
     */
    @Query("SELECT j FROM Job j WHERE j.status = :status AND j.createdAt BETWEEN :startDate AND :endDate")
    List<Job> findByStatusAndCreatedAtBetween(@Param("status") JobStatus status,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Finds jobs by user and date range.
     * 
     * @param requestedBy the user who requested the jobs
     * @param startDate the start date
     * @param endDate the end date
     * @return List<Job> list of jobs matching the criteria
     */
    @Query("SELECT j FROM Job j WHERE j.requestedBy = :requestedBy AND j.createdAt BETWEEN :startDate AND :endDate")
    List<Job> findByRequestedByAndCreatedAtBetween(@Param("requestedBy") User requestedBy,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);
    
    /**
     * Finds jobs that have been running for too long.
     * 
     * @param threshold the time threshold
     * @return List<Job> list of jobs that have been running too long
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'PROCESSING' AND j.startedAt < :threshold")
    List<Job> findStuckJobs(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Finds the oldest pending job for queue processing.
     * 
     * @return Optional<Job> the oldest pending job
     */
    @Query(value = "SELECT * FROM jobs WHERE status = 'PENDING' ORDER BY priority DESC, created_at ASC LIMIT 1", 
           nativeQuery = true)
    Optional<Job> findOldestPendingJob();
    
    /**
     * Counts jobs by status.
     * 
     * @param status the job status
     * @return long count of jobs with the specified status
     */
    long countByStatus(JobStatus status);
    
    /**
     * Counts jobs by requested by user.
     * 
     * @param requestedBy the user who requested the jobs
     * @return long count of jobs requested by the user
     */
    long countByRequestedBy(User requestedBy);
    
    /**
     * Counts jobs by requested by user and status.
     * 
     * @param requestedBy the user who requested the jobs
     * @param status the job status
     * @return long count of jobs matching the criteria
     */
    long countByRequestedByAndStatus(User requestedBy, JobStatus status);
    
    /**
     * Counts jobs by model used.
     * 
     * @param modelUsed the model used for transcription
     * @return long count of jobs using the specified model
     */
    long countByModelUsed(String modelUsed);
    
    /**
     * Calculates average processing time by status.
     * 
     * @param status the job status
     * @return Double average processing time in milliseconds
     */
    @Query("SELECT AVG(j.processingTimeMs) FROM Job j WHERE j.status = :status AND j.processingTimeMs IS NOT NULL")
    Double getAverageProcessingTimeByStatus(@Param("status") JobStatus status);
    
    /**
     * Calculates average processing time by model.
     * 
     * @param modelUsed the model used for transcription
     * @return Double average processing time in milliseconds
     */
    @Query("SELECT AVG(j.processingTimeMs) FROM Job j WHERE j.modelUsed = :modelUsed AND j.processingTimeMs IS NOT NULL")
    Double getAverageProcessingTimeByModel(@Param("modelUsed") String modelUsed);
    
    /**
     * Finds the fastest completed jobs.
     * 
     * @param pageable pagination and sorting information
     * @return Page<Job> page of fastest completed jobs
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'COMPLETED' AND j.processingTimeMs IS NOT NULL ORDER BY j.processingTimeMs ASC")
    Page<Job> findFastestCompletedJobs(Pageable pageable);
    
    /**
     * Finds the slowest completed jobs.
     * 
     * @param pageable pagination and sorting information
     * @return Page<Job> page of slowest completed jobs
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'COMPLETED' AND j.processingTimeMs IS NOT NULL ORDER BY j.processingTimeMs DESC")
    Page<Job> findSlowestCompletedJobs(Pageable pageable);
    
    /**
     * Finds jobs with high priority.
     * 
     * @param minPriority the minimum priority
     * @return List<Job> list of high priority jobs
     */
    @Query("SELECT j FROM Job j WHERE j.priority >= :minPriority ORDER BY j.priority DESC, j.createdAt ASC")
    List<Job> findHighPriorityJobs(@Param("minPriority") Integer minPriority);
    
    /**
     * Finds failed jobs with error messages.
     * 
     * @return List<Job> list of failed jobs
     */
    @Query("SELECT j FROM Job j WHERE j.status = 'FAILED' AND j.errorMessage IS NOT NULL")
    List<Job> findFailedJobsWithErrors();
    
    /**
     * Finds jobs that need to be cleaned up (old completed/failed jobs).
     * 
     * @param threshold the time threshold for cleanup
     * @return List<Job> list of jobs that need cleanup
     */
    @Query("SELECT j FROM Job j WHERE j.status IN ('COMPLETED', 'FAILED', 'CANCELLED') AND j.completedAt < :threshold")
    List<Job> findJobsForCleanup(@Param("threshold") LocalDateTime threshold);
}
