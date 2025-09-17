# Whisperrr Backend - Spring Boot API

The Spring Boot backend service that provides REST API endpoints for audio transcription, manages the database, and communicates with the Python transcription service.

## Features

- 🔄 **RESTful API**: Clean REST endpoints for audio processing
- 🗄️ **Database Integration**: PostgreSQL with Flyway migrations
- 🔐 **CORS Configuration**: Proper cross-origin setup for frontend
- 📊 **Health Monitoring**: Actuator endpoints for monitoring
- 🛡️ **Error Handling**: Comprehensive exception handling
- 📁 **File Upload**: Multipart file upload with validation
- 🔗 **Service Integration**: Communicates with Python transcription service

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- PostgreSQL 12+

### Installation

1. **Setup Database:**
   ```bash
   createdb transcription_db
   psql -c "CREATE USER transcription_user WITH PASSWORD 'transcription_pass';"
   psql -c "GRANT ALL PRIVILEGES ON DATABASE transcription_db TO transcription_user;"
   ```

2. **Build and Run:**
   ```bash
   ./mvnw spring-boot:run
   ```

The service will start on `http://localhost:8080`

## API Endpoints

### Audio Transcription

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/audio/upload` | Upload audio file for transcription |
| `GET` | `/api/audio/status/{jobId}` | Get transcription status |
| `GET` | `/api/audio/result/{jobId}` | Get transcription result |
| `GET` | `/api/audio/health` | Health check |

### Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Application health |
| `GET` | `/actuator/info` | Application info |
| `GET` | `/actuator/metrics` | Application metrics |

## Configuration

### Database Configuration

```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/transcription_db
spring.datasource.username=transcription_user
spring.datasource.password=transcription_pass

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

### CORS Configuration

```properties
# CORS Configuration for Frontend
cors.allowed-origins=http://localhost:3000,http://localhost:3001
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true
```

### Python Service Integration

```properties
# Whisperrr Python Service Configuration
whisperrr.service.url=http://localhost:8000
whisperrr.service.timeout=300000
```

## Project Structure

```
src/main/java/com/shangmin/whisperrr/
├── config/              # Configuration classes
│   ├── CorsConfig.java  # CORS configuration
│   └── JpaConfig.java   # JPA configuration
├── controller/          # REST controllers
│   └── AudioController.java
├── dto/                 # Data Transfer Objects
│   ├── AudioUploadRequest.java
│   ├── AudioUploadResponse.java
│   ├── TranscriptionResultResponse.java
│   └── TranscriptionStatusResponse.java
├── entity/              # JPA entities
│   ├── AudioFile.java
│   ├── Job.java
│   ├── Transcription.java
│   └── User.java
├── enums/               # Enumerations
│   ├── AudioFormat.java
│   ├── JobStatus.java
│   └── UserRole.java
├── exception/           # Exception handling
│   ├── GlobalExceptionHandler.java
│   └── Custom exceptions
├── repository/          # Data repositories
│   ├── AudioFileRepository.java
│   ├── JobRepository.java
│   └── TranscriptionRepository.java
└── service/             # Business logic
    ├── AudioService.java
    └── impl/
        └── AudioServiceImpl.java
```

## Database Schema

The application uses Flyway for database migrations. See `src/main/resources/db/migration/` for migration files:

- `V1__Create_users_table.sql` - User management
- `V2__Create_audio_files_table.sql` - Audio file storage
- `V3__Create_jobs_table.sql` - Transcription jobs
- `V4__Create_transcriptions_table.sql` - Transcription results
- `V5__Create_indexes.sql` - Database indexes

## Development

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package
java -jar target/whisperrr-api-0.0.1-SNAPSHOT.jar
```

### Database Migrations

```bash
# Run migrations
./mvnw flyway:migrate

# Validate migrations
./mvnw flyway:validate
```

## Integration with Python Service

The backend communicates with the Python transcription service via HTTP:

```java
@Service
public class AudioServiceImpl implements AudioService {
    
    @Value("${whisperrr.service.url}")
    private String whisperrrUrl;
    
    public TranscriptionResult transcribeAudio(MultipartFile audioFile) {
        // Send file to Python service
        // Process response
        // Store in database
    }
}
```

## Error Handling

The application includes comprehensive error handling:

- `GlobalExceptionHandler` - Centralized exception handling
- Custom exceptions for different error types
- Proper HTTP status codes
- Detailed error messages

## Monitoring

### Health Checks

- `/actuator/health` - Application health status
- `/actuator/info` - Application information
- `/actuator/metrics` - Performance metrics

### Logging

Configured logging levels:
- `com.shangmin.whisperrr=INFO` - Application logs
- `org.springframework.web=INFO` - Web layer logs
- `org.springframework.multipart=INFO` - File upload logs

## Security

- CORS configuration for frontend access
- File upload validation
- Input validation with Bean Validation
- SQL injection protection via JPA

## Performance

- Connection pooling with HikariCP
- JPA query optimization
- File upload size limits
- Async processing for long operations

## Deployment

### Docker

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/whisperrr-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/transcription_db
export SPRING_DATASOURCE_USERNAME=transcription_user
export SPRING_DATASOURCE_PASSWORD=transcription_pass
export WHISPERRR_SERVICE_URL=http://localhost:8000
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check PostgreSQL is running
   - Verify connection credentials
   - Ensure database exists

2. **Python Service Unavailable**
   - Check Python service is running on port 8000
   - Verify `whisperrr.service.url` configuration

3. **CORS Issues**
   - Check `cors.allowed-origins` configuration
   - Verify frontend URL is included

### Logs

```bash
# View application logs
tail -f logs/application.log

# Enable debug logging
export LOGGING_LEVEL_COM_SHANGMIN_WHISPERRR=DEBUG
```
