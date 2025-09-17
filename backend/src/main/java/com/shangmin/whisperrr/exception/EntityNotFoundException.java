package com.shangmin.whisperrr.exception;

/**
 * Exception thrown when an entity is not found in the database.
 * 
 * @author shangmin
 * @version 1.0
 */
public class EntityNotFoundException extends DatabaseException {
    
    private final String entityType;
    private final Object entityId;
    
    /**
     * Constructs a new entity not found exception.
     * 
     * @param entityType the type of entity that was not found
     * @param entityId the ID of the entity that was not found
     */
    public EntityNotFoundException(String entityType, Object entityId) {
        super(String.format("%s with ID %s not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    /**
     * Constructs a new entity not found exception with a custom message.
     * 
     * @param message the detail message
     * @param entityType the type of entity that was not found
     * @param entityId the ID of the entity that was not found
     */
    public EntityNotFoundException(String message, String entityType, Object entityId) {
        super(message);
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    /**
     * Gets the type of entity that was not found.
     * 
     * @return String the entity type
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Gets the ID of the entity that was not found.
     * 
     * @return Object the entity ID
     */
    public Object getEntityId() {
        return entityId;
    }
}
