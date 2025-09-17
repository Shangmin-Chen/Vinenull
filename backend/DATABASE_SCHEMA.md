# Database Schema Documentation

## Overview

This document describes the PostgreSQL database schema for the Whisperrr audio transcription microservice. The schema is designed to be production-ready with proper relationships, audit trails, and performance optimizations.

## Database Configuration

### Connection Details
- **Database**: `transcription_db`
- **User**: `transcription_user`
- **Password**: `transcription_pass`
- **Host**: `localhost`
- **Port**: `5432`

### Connection Pool Settings
- **Maximum Pool Size**: 20
- **Minimum Idle**: 5
- **Idle Timeout**: 300 seconds
- **Connection Timeout**: 20 seconds
- **Leak Detection Threshold**: 60 seconds

## Entity Relationships

```
User (1) -----> (N) AudioFile
User (1) -----> (N) Job
AudioFile (1) -----> (N) Job
Job (1) -----> (1) Transcription
```

## Tables

### 1. users
Stores user account information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated primary key |
| username | VARCHAR(50) | NOT NULL, UNIQUE | Unique username for login |
| email | VARCHAR(100) | NOT NULL, UNIQUE | Unique email address |
| password_hash | VARCHAR(255) | NOT NULL | Hashed password |
| first_name | VARCHAR(50) | NULL | User's first name |
| last_name | VARCHAR(50) | NULL | User's last name |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | User role (USER, ADMIN, API_USER) |
| is_active | BOOLEAN | NOT NULL, DEFAULT true | Account status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| version | BIGINT | NOT NULL | Optimistic locking version |

**Indexes:**
- `idx_users_username` on `username`
- `idx_users_email` on `email`
- `idx_users_active` on `is_active` (partial index for active users)

### 2. audio_files
Stores information about uploaded audio files.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated primary key |
| filename | VARCHAR(255) | NOT NULL | Internal filename |
| original_filename | VARCHAR(255) | NOT NULL | Original upload filename |
| file_size | BIGINT | NOT NULL, CHECK > 0 | File size in bytes |
| duration | DOUBLE PRECISION | NULL | Audio duration in seconds |
| format | VARCHAR(10) | NOT NULL | Audio format (MP3, WAV, etc.) |
| s3_key | VARCHAR(500) | NULL | S3 storage key |
| checksum | VARCHAR(32) | NULL | MD5 checksum |
| uploaded_by | BIGINT | NOT NULL, FK to users | User who uploaded the file |
| created_at | TIMESTAMP | NOT NULL | Upload timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| version | BIGINT | NOT NULL | Optimistic locking version |

**Foreign Keys:**
- `fk_audio_files_uploaded_by` → `users(id)`

**Indexes:**
- `idx_audio_files_uploaded_by` on `uploaded_by`
- `idx_audio_files_s3_key` on `s3_key`
- `idx_audio_files_format` on `format`
- `idx_audio_files_created_at` on `created_at`

### 3. jobs
Stores transcription job information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated primary key |
| job_id | UUID | NOT NULL, UNIQUE | External job identifier |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Job status |
| priority | INTEGER | NOT NULL, DEFAULT 0 | Processing priority |
| started_at | TIMESTAMP | NULL | Processing start time |
| completed_at | TIMESTAMP | NULL | Processing completion time |
| error_message | TEXT | NULL | Error message if failed |
| processing_time_ms | BIGINT | NULL | Processing time in milliseconds |
| model_used | VARCHAR(50) | NULL | AI model used |
| requested_by | BIGINT | NOT NULL, FK to users | User who requested the job |
| audio_file_id | BIGINT | NOT NULL, FK to audio_files | Audio file to transcribe |
| created_at | TIMESTAMP | NOT NULL | Job creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| version | BIGINT | NOT NULL | Optimistic locking version |

**Foreign Keys:**
- `fk_jobs_requested_by` → `users(id)`
- `fk_jobs_audio_file` → `audio_files(id)`

**Check Constraints:**
- `chk_jobs_status` ensures status is one of: PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED

**Indexes:**
- `idx_jobs_job_id` on `job_id`
- `idx_jobs_status` on `status`
- `idx_jobs_requested_by` on `requested_by`
- `idx_jobs_created_at` on `created_at`
- `idx_jobs_queue` on `(status, priority DESC, created_at ASC)` (partial index for pending jobs)

### 4. transcriptions
Stores transcription results.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PRIMARY KEY | Auto-generated primary key |
| text | TEXT | NOT NULL | Transcribed text |
| language | VARCHAR(10) | NULL | Detected language code |
| confidence | DOUBLE PRECISION | CHECK 0.0-1.0 | Confidence score |
| duration | DOUBLE PRECISION | NULL | Transcription duration |
| segments_json | TEXT | NULL | JSON segments with timestamps |
| job_id | BIGINT | NOT NULL, UNIQUE, FK to jobs | Associated job |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| version | BIGINT | NOT NULL | Optimistic locking version |

**Foreign Keys:**
- `fk_transcriptions_job` → `jobs(id)`

**Indexes:**
- `idx_transcriptions_job_id` on `job_id`
- `idx_transcriptions_language` on `language`
- `idx_transcriptions_confidence` on `confidence`
- `idx_transcriptions_text_search` on `text` (GIN index for full-text search)

## Performance Optimizations

### Composite Indexes
- `idx_jobs_user_status` on `(requested_by, status)`
- `idx_audio_files_user_format` on `(uploaded_by, format)`
- `idx_jobs_status_priority_created` on `(status, priority DESC, created_at ASC)`
- `idx_transcriptions_lang_conf` on `(language, confidence)`

### Partial Indexes
- `idx_jobs_completed_at` on `completed_at` WHERE `status = 'COMPLETED'`
- `idx_jobs_cleanup` on `(status, completed_at)` WHERE `status IN ('COMPLETED', 'FAILED', 'CANCELLED')`
- `idx_users_active_role` on `role` WHERE `is_active = true`

### Full-Text Search
- GIN index on transcription text for efficient full-text search queries

## Data Validation

### Entity-Level Validation
- **User**: Username and email uniqueness, email format validation
- **AudioFile**: File size must be positive, format validation
- **Job**: Status enum validation, UUID uniqueness
- **Transcription**: Confidence score range validation (0.0-1.0)

### Database-Level Constraints
- Foreign key constraints ensure referential integrity
- Check constraints validate enum values and ranges
- Unique constraints prevent duplicate data
- Not null constraints ensure required fields

## Audit Trail

All entities extend `BaseEntity` which provides:
- `created_at`: Automatic timestamp when entity is created
- `updated_at`: Automatic timestamp when entity is updated
- `version`: Optimistic locking version number

## Migration Strategy

The database schema is managed using Flyway migrations:
1. `V1__Create_users_table.sql` - Creates users table
2. `V2__Create_audio_files_table.sql` - Creates audio_files table
3. `V3__Create_jobs_table.sql` - Creates jobs table
4. `V4__Create_transcriptions_table.sql` - Creates transcriptions table
5. `V5__Create_indexes.sql` - Creates performance indexes

## Security Considerations

- Passwords are stored as hashes, never in plain text
- Database user has minimal required permissions
- Connection pooling prevents connection exhaustion
- Optimistic locking prevents concurrent modification issues

## Monitoring and Maintenance

### Recommended Queries for Monitoring
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
- Old completed/failed jobs can be archived or deleted
- Audio files can be moved to cold storage after a retention period
- Transcription text can be compressed for long-term storage

## Backup Strategy

1. **Full Database Backup**: Daily full backups
2. **Transaction Log Backup**: Continuous WAL archiving
3. **Point-in-Time Recovery**: Enabled for disaster recovery
4. **Cross-Region Replication**: For high availability

## Scaling Considerations

- **Read Replicas**: For read-heavy workloads
- **Partitioning**: Consider partitioning large tables by date
- **Connection Pooling**: Use PgBouncer for connection management
- **Caching**: Implement Redis for frequently accessed data
