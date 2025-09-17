package com.shangmin.whisperrr.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Base entity class providing common audit fields for all entities.
 * This class includes ID, creation timestamp, last modification timestamp,
 * and version for optimistic locking.
 * 
 * @author shangmin
 * @version 1.0
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    
    /**
     * Default constructor.
     */
    protected BaseEntity() {
        // Default constructor for JPA
    }
    
    /**
     * Gets the unique identifier of the entity.
     * 
     * @return Long unique identifier
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Sets the unique identifier of the entity.
     * Note: This should typically not be called manually as it's managed by JPA.
     * 
     * @param id the unique identifier
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Gets the creation timestamp of the entity.
     * 
     * @return LocalDateTime when the entity was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp of the entity.
     * Note: This should typically not be called manually as it's managed by JPA auditing.
     * 
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last modification timestamp of the entity.
     * 
     * @return LocalDateTime when the entity was last modified
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last modification timestamp of the entity.
     * Note: This should typically not be called manually as it's managed by JPA auditing.
     * 
     * @param updatedAt the last modification timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the version number for optimistic locking.
     * 
     * @return Long version number
     */
    public Long getVersion() {
        return version;
    }
    
    /**
     * Sets the version number for optimistic locking.
     * Note: This should typically not be called manually as it's managed by JPA.
     * 
     * @param version the version number
     */
    public void setVersion(Long version) {
        this.version = version;
    }
    
    /**
     * Checks if the entity is new (not yet persisted).
     * 
     * @return true if the entity is new, false otherwise
     */
    public boolean isNew() {
        return id == null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", version=" + version +
                '}';
    }
}
