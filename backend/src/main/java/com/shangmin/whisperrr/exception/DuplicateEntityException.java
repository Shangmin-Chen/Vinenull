package com.shangmin.whisperrr.exception;

/**
 * Exception thrown when attempting to create an entity that already exists.
 * 
 * @author shangmin
 * @version 1.0
 */
public class DuplicateEntityException extends DatabaseException {
    
    private final String entityType;
    private final String fieldName;
    private final Object fieldValue;
    
    /**
     * Constructs a new duplicate entity exception.
     * 
     * @param entityType the type of entity that already exists
     * @param fieldName the field name that caused the duplicate
     * @param fieldValue the field value that caused the duplicate
     */
    public DuplicateEntityException(String entityType, String fieldName, Object fieldValue) {
        super(String.format("%s with %s '%s' already exists", entityType, fieldName, fieldValue));
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Constructs a new duplicate entity exception with a custom message.
     * 
     * @param message the detail message
     * @param entityType the type of entity that already exists
     * @param fieldName the field name that caused the duplicate
     * @param fieldValue the field value that caused the duplicate
     */
    public DuplicateEntityException(String message, String entityType, String fieldName, Object fieldValue) {
        super(message);
        this.entityType = entityType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    /**
     * Gets the type of entity that already exists.
     * 
     * @return String the entity type
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Gets the field name that caused the duplicate.
     * 
     * @return String the field name
     */
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Gets the field value that caused the duplicate.
     * 
     * @return Object the field value
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
