# Whisperrr - Audio Transcription Platform

A full-stack audio transcription platform built with Spring Boot, Python FastAPI, and React TypeScript. This project provides high-quality speech-to-text capabilities using OpenAI's Whisper library with a modern web interface.

## 🏗️ Architecture

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    Internal    ┌─────────────────┐
│                 │ ──────────────► │                 │ ──────────────► │                 │
│   React Frontend│                 │ Spring Boot API │                 │ Python Service  │
│   (Port 3000)   │ ◄────────────── │   (Port 8080)   │ ◄────────────── │   (Port 8000)   │
│                 │                 │                 │                 │                 │
└─────────────────┘                 └─────────────────┘                 └─────────────────┘
```

## 🚀 Features

### Frontend (React + TypeScript)
- 🎯 **Modern UI**: Beautiful, responsive design with drag-and-drop file upload
- 📊 **Real-time Updates**: Live transcription status with automatic polling
- 📝 **Results Display**: Clean transcription results with timing information
- 📱 **Mobile Ready**: Fully responsive design
- 🎨 **Dark/Light Mode**: Theme switching support

### Backend (Spring Boot)
- 🔄 **RESTful API**: Clean REST endpoints for audio processing
- 🗄️ **Database Integration**: PostgreSQL with Flyway migrations
- 🔐 **CORS Configuration**: Proper cross-origin setup for frontend
- 📊 **Health Monitoring**: Actuator endpoints for monitoring
- 🛡️ **Error Handling**: Comprehensive exception handling

### Python Service (FastAPI)
- 🎯 **High-Quality Transcription**: OpenAI Whisper models
- 🚀 **Async Processing**: Non-blocking transcription with ThreadPoolExecutor
- 📁 **Multiple Formats**: MP3, WAV, M4A, FLAC, OGG, WMA support
- 🔧 **Model Management**: Dynamic model loading and caching
- 📊 **Comprehensive Monitoring**: Health checks, metrics, and performance logging

## 📁 Project Structure

```
Whisperrr/
├── backend/                 # Spring Boot API
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   ├── pom.xml            # Maven dependencies
│   └── README.md          # Backend documentation
├── python-service/         # Python FastAPI service
│   ├── app/               # Python source code
│   ├── requirements.txt   # Python dependencies
│   ├── venv/              # Virtual environment
│   └── README.md          # Python service documentation
├── frontend/              # React TypeScript frontend
│   ├── src/               # React source code
│   ├── package.json       # Node.js dependencies
│   └── README.md          # Frontend documentation
└── README.md              # This file
```

## 🛠️ Quick Start

### Prerequisites

- **Java 21+** (for Spring Boot backend)
- **Python 3.8+** (for transcription service)
- **Node.js 18+** (for React frontend)
- **PostgreSQL** (for database)

### 1. Database Setup

```bash
# Create PostgreSQL database
createdb transcription_db

# Create user (optional)
psql -c "CREATE USER transcription_user WITH PASSWORD 'transcription_pass';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE transcription_db TO transcription_user;"
```

### 2. Backend Setup (Spring Boot)

```bash
cd backend

# Build and run
./mvnw spring-boot:run

# Or with Maven
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### 3. Python Service Setup

```bash
cd python-service

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run the service
python -m app.main
```

Python service will start on `http://localhost:8000`

### 4. Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

Frontend will start on `http://localhost:3000`

## 🔧 Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/transcription_db
spring.datasource.username=transcription_user
spring.datasource.password=transcription_pass

# CORS for frontend
cors.allowed-origins=http://localhost:3000,http://localhost:3001

# Python service
whisperrr.service.url=http://localhost:8000
```

### Python Service Configuration

Copy `python-service/env.example` to `.env` and configure:

```bash
MODEL_SIZE=base
MAX_FILE_SIZE_MB=25
CORS_ORIGINS=http://localhost:8080,http://localhost:3000
```

### Frontend Configuration

Copy `frontend/env.example` to `.env` and configure:

```bash
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_POLLING_INTERVAL=2000
REACT_APP_MAX_FILE_SIZE=25
```

## 📡 API Endpoints

### Spring Boot Backend (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/audio/upload` | Upload audio file for transcription |
| `GET` | `/api/audio/status/{jobId}` | Get transcription status |
| `GET` | `/api/audio/result/{jobId}` | Get transcription result |
| `GET` | `/api/audio/health` | Health check |

### Python Service (Port 8000)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/transcribe` | Direct transcription (used by backend) |
| `GET` | `/health` | Service health check |
| `GET` | `/model/info` | Model information |
| `POST` | `/model/load/{model_size}` | Load specific model |

## 🎯 Usage

1. **Start all services** (backend, python-service, frontend)
2. **Open browser** to `http://localhost:3000`
3. **Upload audio file** using drag-and-drop or file picker
4. **Monitor progress** with real-time status updates
5. **View results** when transcription completes

## 🚀 Deployment

### Development
All services run locally with hot reload enabled.

### Production
- **Frontend**: Deploy to Vercel, Netlify, or static hosting
- **Backend**: Deploy to AWS, Google Cloud, or any Java hosting
- **Python Service**: Deploy to AWS Lambda, Google Cloud Functions, or container hosting
- **Database**: Use managed PostgreSQL service

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### Python Service Tests
```bash
cd python-service
python -m pytest
```

## 📊 Monitoring

### Health Checks
- Backend: `http://localhost:8080/actuator/health`
- Python Service: `http://localhost:8000/health`
- Frontend: Built-in React development tools

### Metrics
- Backend: `http://localhost:8080/actuator/metrics`
- Python Service: `http://localhost:8000/metrics`

## 🔧 Development

### Adding New Features

1. **Backend**: Add new endpoints in `AudioController.java`
2. **Python Service**: Add new endpoints in `main.py`
3. **Frontend**: Add new components in `src/components/`

### Code Style

- **Java**: Follow Spring Boot conventions
- **Python**: Follow PEP 8 with Black formatting
- **TypeScript**: Follow React/TypeScript best practices

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support & Troubleshooting

### Common Issues

**Whisper Installation Issues:**
If you're having trouble installing the whisper package:

1. **Try with Python 3.11 or 3.12** (Python 3.13 has compatibility issues):
   ```bash
   pyenv install 3.11.7
   pyenv local 3.11.7
   python -m venv venv
   source venv/bin/activate
   pip install -r requirements.txt
   ```

2. **Install from conda** (if you have conda):
   ```bash
   conda install -c conda-forge openai-whisper
   ```

3. **Use Docker** (recommended for production):
   ```bash
   docker-compose up --build
   ```

**Database Connection Issues:**
- Ensure PostgreSQL is running
- Check connection credentials in `backend/src/main/resources/application.properties`
- Verify database exists: `createdb transcription_db`

**CORS Issues:**
- Check `cors.allowed-origins` in backend configuration
- Verify frontend URL is included in allowed origins

### Getting Help

For issues and questions:
- Create an issue in the repository
- Check the individual README files in each service directory
- Review the API documentation at `/docs` (Python service)

## 🔗 Links

- [Backend Documentation](backend/README.md)
- [Python Service Documentation](python-service/README.md)
- [Frontend Documentation](frontend/README.md)
- [Database Schema](backend/DATABASE_SCHEMA.md)