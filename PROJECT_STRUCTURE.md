# Whisperrr Project Structure

This document describes the clean, organized structure of the Whisperrr audio transcription platform.

## 📁 Root Directory

```
Whisperrr/
├── README.md                 # Main project documentation
├── LICENSE                   # MIT License
├── setup.sh                  # Development setup script
├── docker-compose.yml        # Full-stack Docker deployment
├── .gitignore               # Comprehensive git ignore rules
└── PROJECT_STRUCTURE.md     # This file
```

## 🏗️ Service Directories

### Backend (Spring Boot API)
```
backend/
├── README.md                    # Backend-specific documentation
├── pom.xml                     # Maven dependencies
├── Dockerfile                  # Container configuration
├── mvnw, mvnw.cmd             # Maven wrapper scripts
├── database-setup.sql          # Database initialization
├── DATABASE_README.md          # Database documentation
├── DATABASE_SCHEMA.md          # Database schema reference
└── src/
    ├── main/
    │   ├── java/com/shangmin/whisperrr/
    │   │   ├── config/         # Configuration classes
    │   │   ├── controller/     # REST controllers
    │   │   ├── dto/           # Data Transfer Objects
    │   │   ├── entity/        # JPA entities
    │   │   ├── enums/         # Enumerations
    │   │   ├── exception/     # Exception handling
    │   │   ├── repository/    # Data repositories
    │   │   ├── service/       # Business logic
    │   │   └── WhisperrrApiApplication.java
    │   └── resources/
    │       ├── application.properties  # Configuration
    │       └── db/migration/          # Database migrations
    └── test/                   # Test files
```

### Python Service (FastAPI)
```
python-service/
├── README.md                  # Python service documentation
├── requirements.txt           # Python dependencies
├── Dockerfile                 # Container configuration
├── env.example               # Environment variables template
├── venv/                     # Python virtual environment
└── app/                      # Python source code
    ├── __init__.py
    ├── main.py               # FastAPI application
    ├── config.py             # Configuration management
    ├── models.py             # Pydantic models
    ├── whisper_service.py    # Whisper model management
    ├── exceptions.py         # Custom exceptions
    └── utils.py              # Utility functions
```

### Frontend (React TypeScript)
```
frontend/
├── README.md                  # Frontend documentation
├── package.json              # Node.js dependencies
├── Dockerfile                # Container configuration
├── nginx.conf                # Nginx configuration
├── env.example               # Environment variables template
└── src/                      # React source code
    ├── components/           # React components
    │   ├── common/          # Reusable components
    │   ├── upload/          # File upload components
    │   ├── status/          # Status display components
    │   └── results/         # Results display components
    ├── pages/               # Page components
    ├── services/            # API communication
    │   ├── api.ts           # API client configuration
    │   └── transcription.ts # Transcription API calls
    ├── types/               # TypeScript interfaces
    │   ├── api.ts           # API response types
    │   └── transcription.ts # Transcription types
    ├── hooks/               # Custom React hooks
    ├── utils/               # Helper functions
    └── styles/              # Global styles and themes
```

## 🚀 Key Features

### Clean Architecture
- **Separation of Concerns**: Each service has its own directory and responsibility
- **Monorepo Structure**: All services in one repository for easy development
- **Docker Ready**: Each service has its own Dockerfile + docker-compose.yml

### Development Ready
- **Setup Script**: `./setup.sh` sets up the entire development environment
- **Environment Templates**: `env.example` files for easy configuration
- **Comprehensive Documentation**: README files for each service

### Production Ready
- **Docker Support**: Full containerization with docker-compose
- **Database Migrations**: Flyway migrations for database schema management
- **CORS Configuration**: Proper cross-origin setup for frontend communication
- **Health Checks**: Monitoring endpoints for all services

## 🔧 Development Workflow

1. **Setup**: Run `./setup.sh` to set up the development environment
2. **Start Services**: Use the provided commands to start each service
3. **Develop**: Each service can be developed independently
4. **Test**: Use the provided test commands for each service
5. **Deploy**: Use Docker Compose for production deployment

## 📊 Service Communication

```
Frontend (React) → Backend (Spring Boot) → Python Service (FastAPI)
     ↓                    ↓                        ↓
  Port 3000           Port 8080                Port 8000
```

- **Frontend ↔ Backend**: REST API calls over HTTP
- **Backend ↔ Python**: Internal HTTP calls for transcription
- **All Services**: Can be run independently or together

## 🗂️ File Organization Principles

1. **Service Separation**: Each service in its own directory
2. **Configuration**: Environment-specific configs in each service
3. **Documentation**: README files for each service + main project README
4. **Dependencies**: Each service manages its own dependencies
5. **Build Artifacts**: Excluded via .gitignore, not committed
6. **Templates**: Example files for easy setup

This structure provides a clean, maintainable, and scalable foundation for the Whisperrr audio transcription platform.
