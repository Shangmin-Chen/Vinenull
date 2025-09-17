package com.shangmin.whisperrr.entity;

import com.shangmin.whisperrr.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing a user in the transcription system.
 * 
 * @author shangmin
 * @version 1.0
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_users_email", columnNames = "email")
       },
       indexes = {
           @Index(name = "idx_users_username", columnList = "username"),
           @Index(name = "idx_users_email", columnList = "email"),
           @Index(name = "idx_users_active", columnList = "is_active")
       })
public class User extends BaseEntity {
    
    @NotBlank(message = "Username is required")
    @Size(max = 50, message = "Username must not exceed 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @NotBlank(message = "Password hash is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role = UserRole.USER;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AudioFile> audioFiles = new ArrayList<>();
    
    @OneToMany(mappedBy = "requestedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();
    
    /**
     * Default constructor.
     */
    public User() {
        // Default constructor for JPA
    }
    
    /**
     * Constructor with required fields.
     * 
     * @param username the username
     * @param email the email address
     * @param passwordHash the hashed password
     */
    public User(String username, String email, String passwordHash) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }
    
    /**
     * Gets the username.
     * 
     * @return String username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the email address.
     * 
     * @return String email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address.
     * 
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the password hash.
     * 
     * @return String password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Sets the password hash.
     * 
     * @param passwordHash the password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * Gets the first name.
     * 
     * @return String first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name.
     * 
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the last name.
     * 
     * @return String last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name.
     * 
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the user role.
     * 
     * @return UserRole the user role
     */
    public UserRole getRole() {
        return role;
    }
    
    /**
     * Sets the user role.
     * 
     * @param role the user role
     */
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    /**
     * Gets whether the user is active.
     * 
     * @return Boolean true if active, false otherwise
     */
    public Boolean getIsActive() {
        return isActive;
    }
    
    /**
     * Sets whether the user is active.
     * 
     * @param isActive true if active, false otherwise
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    /**
     * Gets the list of audio files uploaded by this user.
     * 
     * @return List<AudioFile> list of audio files
     */
    public List<AudioFile> getAudioFiles() {
        return audioFiles;
    }
    
    /**
     * Sets the list of audio files uploaded by this user.
     * 
     * @param audioFiles the list of audio files
     */
    public void setAudioFiles(List<AudioFile> audioFiles) {
        this.audioFiles = audioFiles;
    }
    
    /**
     * Gets the list of jobs requested by this user.
     * 
     * @return List<Job> list of jobs
     */
    public List<Job> getJobs() {
        return jobs;
    }
    
    /**
     * Sets the list of jobs requested by this user.
     * 
     * @param jobs the list of jobs
     */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
    
    /**
     * Gets the full name of the user.
     * 
     * @return String full name or username if names are not available
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username;
    }
    
    /**
     * Checks if the user is an administrator.
     * 
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    /**
     * Checks if the user has API access.
     * 
     * @return true if the user has API access, false otherwise
     */
    public boolean hasApiAccess() {
        return role != null && role.hasApiAccess();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
