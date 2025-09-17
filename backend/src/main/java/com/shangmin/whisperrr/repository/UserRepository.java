package com.shangmin.whisperrr.repository;

import com.shangmin.whisperrr.entity.User;
import com.shangmin.whisperrr.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * @author shangmin
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by username.
     * 
     * @param username the username
     * @return Optional<User> the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by email.
     * 
     * @param email the email address
     * @return Optional<User> the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds a user by username or email.
     * 
     * @param username the username
     * @param email the email address
     * @return Optional<User> the user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    /**
     * Checks if a user exists with the given username.
     * 
     * @param username the username
     * @return boolean true if user exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Checks if a user exists with the given email.
     * 
     * @param email the email address
     * @return boolean true if user exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds users by role.
     * 
     * @param role the user role
     * @return List<User> list of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Finds active users.
     * 
     * @return List<User> list of active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Finds inactive users.
     * 
     * @return List<User> list of inactive users
     */
    List<User> findByIsActiveFalse();
    
    /**
     * Finds users by role and active status.
     * 
     * @param role the user role
     * @param isActive the active status
     * @return List<User> list of users matching the criteria
     */
    List<User> findByRoleAndIsActive(UserRole role, Boolean isActive);
    
    /**
     * Finds users with pagination and sorting.
     * 
     * @param pageable pagination and sorting information
     * @return Page<User> page of users
     */
    Page<User> findAll(Pageable pageable);
    
    /**
     * Finds active users with pagination and sorting.
     * 
     * @param pageable pagination and sorting information
     * @return Page<User> page of active users
     */
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Finds users by role with pagination and sorting.
     * 
     * @param role the user role
     * @param pageable pagination and sorting information
     * @return Page<User> page of users with the specified role
     */
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    /**
     * Searches users by username or email containing the given text.
     * 
     * @param searchText the text to search for
     * @param pageable pagination and sorting information
     * @return Page<User> page of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<User> searchUsers(@Param("searchText") String searchText, Pageable pageable);
    
    /**
     * Finds users created within a date range.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return List<User> list of users created in the date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                     @Param("endDate") java.time.LocalDateTime endDate);
    
    /**
     * Counts users by role.
     * 
     * @param role the user role
     * @return long count of users with the specified role
     */
    long countByRole(UserRole role);
    
    /**
     * Counts active users.
     * 
     * @return long count of active users
     */
    long countByIsActiveTrue();
    
    /**
     * Counts inactive users.
     * 
     * @return long count of inactive users
     */
    long countByIsActiveFalse();
    
    /**
     * Finds users with the most recent activity.
     * 
     * @param limit the maximum number of users to return
     * @return List<User> list of users ordered by last update time
     */
    @Query("SELECT u FROM User u ORDER BY u.updatedAt DESC")
    List<User> findMostRecentlyActiveUsers(Pageable pageable);
    
    /**
     * Finds users who have uploaded audio files.
     * 
     * @return List<User> list of users who have uploaded files
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.audioFiles af WHERE af IS NOT NULL")
    List<User> findUsersWithAudioFiles();
    
    /**
     * Finds users who have requested transcription jobs.
     * 
     * @return List<User> list of users who have requested jobs
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.jobs j WHERE j IS NOT NULL")
    List<User> findUsersWithJobs();
}
