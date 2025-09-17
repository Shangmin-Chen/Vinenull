# Whisperrr Database Setup Guide

## Overview

This guide provides complete instructions for setting up the PostgreSQL database for the Whisperrr audio transcription microservice. The database schema is production-ready with proper relationships, audit trails, and performance optimizations.

## Prerequisites

- PostgreSQL 12+ installed and running
- Java 21+
- Maven 3.6+
- Spring Boot 3.5.5

## Quick Start

### 1. Database Setup

```bash
# Connect to PostgreSQL as superuser
psql -U postgres

# Run the database setup script
\i database-setup.sql
```

### 2. Application Configuration

The application is already configured with the following database settings in `application.properties`:

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/transcription_db
spring.datasource.username=transcription_user
spring.datasource.password=transcription_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
```

### 3. Run the Application

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

Flyway will automatically run the migration scripts and create the database schema.

## Database Schema

### Tables Created

1. **users** - User accounts and authentication
2. **audio_files** - Uploaded audio files metadata
3. **jobs** - Transcription job queue and status
4. **transcriptions** - Transcription results and metadata

### Key Features

- **Audit Trails**: All entities have `created_at`, `updated_at`, and `version` fields
- **Optimistic Locking**: Version field prevents concurrent modification issues
- **Performance Indexes**: Optimized for common query patterns
- **Full-Text Search**: GIN index on transcription text
- **Data Validation**: Database-level constraints and JPA validation

## Migration Scripts

The following Flyway migration scripts are included:

- `V1__Create_users_table.sql` - Creates users table with indexes
- `V2__Create_audio_files_table.sql` - Creates audio_files table with foreign keys
- `V3__Create_jobs_table.sql` - Creates jobs table with constraints
- `V4__Create_transcriptions_table.sql` - Creates transcriptions table with full-text search
- `V5__Create_indexes.sql` - Creates additional performance indexes

## Entity Classes

### BaseEntity
All entities extend `BaseEntity` which provides:
- `id`: Auto-generated primary key
- `createdAt`: Automatic creation timestamp
- `updatedAt`: Automatic update timestamp
- `version`: Optimistic locking version

### User Entity
```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Boolean isActive;
    // ... relationships and methods
}
```

### AudioFile Entity
```java
@Entity
@Table(name = "audio_files")
public class AudioFile extends BaseEntity {
    private String filename;
    private String originalFilename;
    private Long fileSize;
    private Double duration;
    private AudioFormat format;
    private String s3Key;
    private String checksum;
    private User uploadedBy;
    // ... relationships and methods
}
```

### Job Entity
```java
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {
    private UUID jobId;
    private JobStatus status;
    private Integer priority;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private Long processingTimeMs;
    private String modelUsed;
    private User requestedBy;
    private AudioFile audioFile;
    // ... relationships and methods
}
```

### Transcription Entity
```java
@Entity
@Table(name = "transcriptions")
public class Transcription extends BaseEntity {
    private String text;
    private String language;
    private Double confidence;
    private Double duration;
    private String segmentsJson;
    private Job job;
    // ... relationships and methods
}
```

## Repository Interfaces

### UserRepository
- `findByUsername(String username)`
- `findByEmail(String email)`
- `findByUsernameOrEmail(String username, String email)`
- `existsByUsername(String username)`
- `existsByEmail(String email)`
- `searchUsers(String searchText, Pageable pageable)`

### AudioFileRepository
- `findByUploadedBy(User uploadedBy)`
- `findByS3Key(String s3Key)`
- `findByFormatAndUploadedBy(AudioFormat format, User uploadedBy)`
- `countByUploadedBy(User uploadedBy)`
- `sumFileSizeByUploadedBy(User uploadedBy)`

### JobRepository
- `findByJobId(UUID jobId)`
- `findByStatus(JobStatus status)`
- `findPendingJobs()`
- `findByRequestedByAndStatus(User requestedBy, JobStatus status)`
- `findOldestPendingJob()` (native query for queue processing)

### TranscriptionRepository
- `findByJobJobId(UUID jobId)`
- `searchByTextContaining(String searchText, Pageable pageable)`
- `fullTextSearch(String searchText, Pageable pageable)`
- `findByLanguage(String language)`
- `findByConfidenceGreaterThan(Double confidence)`

## Exception Handling

Custom database exceptions are provided:

- `DatabaseException` - Base exception for database errors
- `EntityNotFoundException` - When an entity is not found
- `DuplicateEntityException` - When attempting to create a duplicate entity
- `ConcurrentModificationException` - When optimistic locking fails

These are handled by the `GlobalExceptionHandler` with appropriate HTTP status codes.

## Performance Optimizations

### Indexes
- Single column indexes on frequently queried fields
- Composite indexes for common query patterns
- Partial indexes for filtered queries
- GIN index for full-text search

### Connection Pooling
- HikariCP with optimized settings
- Maximum pool size: 20
- Minimum idle: 5
- Connection timeout: 20 seconds

### Query Optimization
- Lazy loading for relationships
- Fetch joins for common patterns
- Native queries for complex operations

## Monitoring and Maintenance

### Useful Queries

```sql
-- Job status distribution
SELECT status, COUNT(*) FROM jobs GROUP BY status;

-- Average processing time by model
SELECT model_used, AVG(processing_time_ms) 
FROM jobs 
WHERE status = 'COMPLETED' 
GROUP BY model_used;

-- User activity
SELECT COUNT(*) as total_users, 
       COUNT(*) FILTER (WHERE is_active = true) as active_users
FROM users;

-- Storage usage by format
SELECT format, COUNT(*), SUM(file_size) 
FROM audio_files 
GROUP BY format;
```

### Cleanup Operations

```sql
-- Archive old completed jobs (older than 90 days)
DELETE FROM jobs 
WHERE status IN ('COMPLETED', 'FAILED', 'CANCELLED') 
AND completed_at < CURRENT_TIMESTAMP - INTERVAL '90 days';

-- Find duplicate files by checksum
SELECT checksum, COUNT(*) 
FROM audio_files 
WHERE checksum IS NOT NULL 
GROUP BY checksum 
HAVING COUNT(*) > 1;
```

## Security Considerations

- Database user has minimal required permissions
- Passwords are hashed using secure algorithms
- Connection pooling prevents connection exhaustion
- Optimistic locking prevents race conditions
- Input validation at both application and database levels

## Backup and Recovery

### Backup Strategy
1. **Full Database Backup**: Daily full backups
2. **Transaction Log Backup**: Continuous WAL archiving
3. **Point-in-Time Recovery**: Enabled for disaster recovery

### Backup Commands
```bash
# Full database backup
pg_dump -U postgres -h localhost transcription_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore from backup
psql -U postgres -h localhost transcription_db < backup_file.sql
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Check if PostgreSQL is running
   - Verify connection parameters in application.properties

2. **Migration Failures**
   - Check Flyway logs for specific errors
   - Ensure database user has CREATE permissions

3. **Performance Issues**
   - Monitor slow query logs
   - Check index usage with EXPLAIN ANALYZE
   - Consider adding additional indexes

4. **Concurrent Modification Errors**
   - Implement retry logic in application code
   - Consider using pessimistic locking for critical operations

### Logs and Monitoring

```bash
# Check PostgreSQL logs
tail -f /var/log/postgresql/postgresql-*.log

# Monitor active connections
SELECT * FROM pg_stat_activity WHERE datname = 'transcription_db';

# Check database size
SELECT pg_size_pretty(pg_database_size('transcription_db'));
```

## Development vs Production

### Development
- Use H2 in-memory database for testing
- Enable SQL logging for debugging
- Use create-drop for schema management

### Production
- Use PostgreSQL with proper connection pooling
- Disable SQL logging for performance
- Use validate for schema management
- Implement proper backup and monitoring

## Support

For issues or questions:
1. Check the logs for specific error messages
2. Review the database schema documentation
3. Consult the PostgreSQL documentation
4. Check Spring Boot and JPA documentation

## License

This database schema is part of the Whisperrr project and follows the same license terms.
