# Whisperrr Python Microservice

A production-ready Python FastAPI microservice for audio transcription using OpenAI's Whisper library.

## Features

- 🎯 **High-Quality Transcription**: Uses OpenAI Whisper models for accurate speech-to-text
- 🚀 **Async Processing**: Non-blocking transcription with ThreadPoolExecutor
- 📁 **Multiple Formats**: Supports MP3, WAV, M4A, FLAC, OGG, WMA
- 🔧 **Model Management**: Dynamic model loading and caching
- 📊 **Comprehensive Monitoring**: Health checks, metrics, and performance logging
- 🛡️ **Production Ready**: Error handling, resource management, and security features
- 🔄 **RESTful API**: Clean REST endpoints with OpenAPI documentation

## Quick Start

### Prerequisites

- Python 3.8+
- pip or conda
- At least 2GB RAM (4GB+ recommended for larger models)

### Installation

1. **Create virtual environment:**
   ```bash
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   ```

2. **Install dependencies:**
   ```bash
   pip install -r requirements.txt
   ```

3. **Run the service:**
   ```bash
   python -m app.main
   ```

The service will start on `http://localhost:8000` with automatic model loading.

## API Documentation

Once running, visit:
- **Interactive API Docs**: http://localhost:8000/docs
- **ReDoc Documentation**: http://localhost:8000/redoc

## API Endpoints

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/transcribe` | Transcribe audio file |
| `GET` | `/health` | Service health check |
| `GET` | `/model/info` | Current model information |
| `POST` | `/model/load/{model_size}` | Load specific model |
| `GET` | `/models/available` | List available models |
| `GET` | `/` | API information |

## Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `MODEL_SIZE` | `base` | Default Whisper model size |
| `MAX_FILE_SIZE_MB` | `25` | Maximum file size in MB |
| `UPLOAD_DIR` | `/tmp/whisperrr_uploads` | Temporary file directory |
| `LOG_LEVEL` | `INFO` | Logging level |
| `MAX_CONCURRENT_TRANSCRIPTIONS` | `3` | Max concurrent transcriptions |
| `CORS_ORIGINS` | `http://localhost:8080` | Allowed CORS origins |

### Model Sizes

| Model | Size | Speed | Accuracy | Use Case |
|-------|------|-------|----------|----------|
| `tiny` | 39 MB | Fastest | Lowest | Quick testing |
| `base` | 74 MB | Fast | Good | General use |
| `small` | 244 MB | Medium | Better | Balanced |
| `medium` | 769 MB | Slow | Good | High accuracy |
| `large` | 1550 MB | Slowest | Best | Maximum accuracy |

## Development

### Project Structure

```
app/
├── __init__.py          # Package initialization
├── main.py              # FastAPI application
├── models.py            # Pydantic data models
├── whisper_service.py   # Whisper model management
├── config.py            # Configuration management
├── exceptions.py        # Custom exceptions
└── utils.py             # Utility functions
```

### Running in Development

```bash
# Install development dependencies
pip install -r requirements.txt

# Run with auto-reload
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000

# Or use the built-in runner
python -m app.main
```

## Production Deployment

### Docker Deployment

```dockerfile
FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./app/

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
```

### Environment Configuration

```bash
# Production environment variables
export MODEL_SIZE=base
export MAX_FILE_SIZE_MB=50
export LOG_LEVEL=INFO
export MAX_CONCURRENT_TRANSCRIPTIONS=5
export UPLOAD_DIR=/app/uploads
```

## Performance Considerations

### Memory Usage

- **Tiny Model**: ~1GB RAM
- **Base Model**: ~1.5GB RAM  
- **Small Model**: ~2GB RAM
- **Medium Model**: ~5GB RAM
- **Large Model**: ~10GB RAM

### Optimization Tips

1. **Model Selection**: Use `base` for general use, `large` for maximum accuracy
2. **Concurrent Processing**: Adjust `MAX_CONCURRENT_TRANSCRIPTIONS` based on available RAM
3. **File Size Limits**: Set appropriate `MAX_FILE_SIZE_MB` for your use case
4. **Cleanup**: Enable `CLEANUP_TEMP_FILES=true` to prevent disk space issues

## Troubleshooting

### Common Issues

1. **Model Loading Fails**
   - Check available disk space (models are downloaded on first use)
   - Verify internet connection for model download
   - Check available RAM for model size

2. **Transcription Fails**
   - Verify audio file format is supported
   - Check file size limits
   - Ensure audio file is not corrupted

3. **Memory Issues**
   - Reduce `MAX_CONCURRENT_TRANSCRIPTIONS`
   - Use smaller model size
   - Monitor memory usage via `/metrics`

### Logs

The service provides structured logging with different levels:

```bash
# Enable debug logging
export LOG_LEVEL=DEBUG

# View logs
tail -f logs/whisperrr.log
```
