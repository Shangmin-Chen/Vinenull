package com.shangmin.whisperrr.enums;

/**
 * Enum representing user roles in the transcription system.
 * 
 * @author shangmin
 * @version 1.0
 */
public enum UserRole {
    USER("Standard user"),
    ADMIN("Administrator"),
    API_USER("API access user");
    
    private final String description;
    
    /**
     * Constructor for UserRole enum.
     * 
     * @param description Human-readable description of the role
     */
    UserRole(String description) {
        this.description = description;
    }
    
    /**
     * Gets the description of the user role.
     * 
     * @return String description of the role
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Checks if the role has administrative privileges.
     * 
     * @return true if the role is ADMIN, false otherwise
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Checks if the role has API access.
     * 
     * @return true if the role has API access, false otherwise
     */
    public boolean hasApiAccess() {
        return this == API_USER || this == ADMIN;
    }
}
