package com.shangmin.whisperrr.exception;

/**
 * Exception thrown when a concurrent modification is detected during optimistic locking.
 * 
 * @author shangmin
 * @version 1.0
 */
public class ConcurrentModificationException extends DatabaseException {
    
    private final String entityType;
    private final Object entityId;
    private final Long expectedVersion;
    private final Long actualVersion;
    
    /**
     * Constructs a new concurrent modification exception.
     * 
     * @param entityType the type of entity that was modified
     * @param entityId the ID of the entity that was modified
     * @param expectedVersion the expected version number
     * @param actualVersion the actual version number
     */
    public ConcurrentModificationException(String entityType, Object entityId, Long expectedVersion, Long actualVersion) {
        super(String.format("Concurrent modification detected for %s with ID %s. Expected version %d, but actual version is %d", 
                           entityType, entityId, expectedVersion, actualVersion));
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }
    
    /**
     * Constructs a new concurrent modification exception with a custom message.
     * 
     * @param message the detail message
     * @param entityType the type of entity that was modified
     * @param entityId the ID of the entity that was modified
     * @param expectedVersion the expected version number
     * @param actualVersion the actual version number
     */
    public ConcurrentModificationException(String message, String entityType, Object entityId, Long expectedVersion, Long actualVersion) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }
    
    /**
     * Gets the type of entity that was modified.
     * 
     * @return String the entity type
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Gets the ID of the entity that was modified.
     * 
     * @return Object the entity ID
     */
    public Object getEntityId() {
        return entityId;
    }
    
    /**
     * Gets the expected version number.
     * 
     * @return Long the expected version
     */
    public Long getExpectedVersion() {
        return expectedVersion;
    }
    
    /**
     * Gets the actual version number.
     * 
     * @return Long the actual version
     */
    public Long getActualVersion() {
        return actualVersion;
    }
}
