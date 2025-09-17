"""
FastAPI application for the Whisperrr transcription service.
"""

import logging
import time
import uuid
from contextlib import asynccontextmanager

from fastapi import FastAPI, File, UploadFile, HTTPException, Depends, Query, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
import uvicorn

from .config import settings
from .models import (
    TranscriptionRequest,
    TranscriptionResponse,
    ModelInfoResponse,
    HealthResponse,
    ErrorResponse
)
from .whisper_service import whisper_service
from .exceptions import WhisperrrException
from .utils import (
    get_correlation_id,
    get_memory_usage,
    log_performance_metrics,
    safe_filename
)

# Configure logging
logging.basicConfig(
    level=getattr(logging, settings.log_level),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan manager for startup and shutdown events."""
    # Startup
    logger.info("Starting Whisperrr transcription service")
    start_time = time.time()
    
    try:
        # Load default model
        logger.info(f"Loading default model: {settings.model_size}")
        await whisper_service.load_model(settings.model_size)
        
        startup_time = time.time() - start_time
        logger.info(f"Service started successfully in {startup_time:.2f}s")
        
        yield
        
    except Exception as e:
        logger.error(f"Failed to start service: {e}")
        raise
    
    finally:
        # Shutdown
        logger.info("Shutting down Whisperrr transcription service")
        await whisper_service.cleanup()
        logger.info("Service shutdown completed")


# Create FastAPI application
app = FastAPI(
    title=settings.api_title,
    description=settings.api_description,
    version=settings.api_version,
    lifespan=lifespan
)

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.cors_origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# Middleware for request logging and correlation IDs
@app.middleware("http")
async def logging_middleware(request: Request, call_next):
    """Middleware for request logging and correlation ID tracking."""
    correlation_id = get_correlation_id()
    request.state.correlation_id = correlation_id
    
    start_time = time.time()
    
    # Log request
    logger.info(
        f"Request started: {request.method} {request.url.path} "
        f"[{correlation_id}]"
    )
    
    try:
        response = await call_next(request)
        
        # Log response
        duration = time.time() - start_time
        logger.info(
            f"Request completed: {request.method} {request.url.path} "
            f"[{correlation_id}] - {response.status_code} - {duration:.3f}s"
        )
        
        # Add correlation ID to response headers
        response.headers["X-Correlation-ID"] = correlation_id
        
        return response
    
    except Exception as e:
        duration = time.time() - start_time
        logger.error(
            f"Request failed: {request.method} {request.url.path} "
            f"[{correlation_id}] - {duration:.3f}s - {str(e)}"
        )
        raise


# Global exception handler
@app.exception_handler(WhisperrrException)
async def whisperrr_exception_handler(request: Request, exc: WhisperrrException):
    """Handle custom Whisperrr exceptions."""
    correlation_id = getattr(request.state, 'correlation_id', None)
    
    error_response = ErrorResponse(
        error_type=exc.error_code or "WHISPERRR_ERROR",
        message=exc.message,
        details=exc.details,
        correlation_id=correlation_id
    )
    
    # Map error codes to HTTP status codes
    status_code_map = {
        "INVALID_AUDIO_FORMAT": 400,
        "FILE_TOO_LARGE": 413,
        "MODEL_NOT_LOADED": 503,
        "TRANSCRIPTION_FAILED": 500,
        "MODEL_LOAD_FAILED": 500,
        "AUDIO_PROCESSING_ERROR": 400,
        "FILE_SYSTEM_ERROR": 500
    }
    
    status_code = status_code_map.get(exc.error_code, 500)
    
    return JSONResponse(
        status_code=status_code,
        content=error_response.dict()
    )


@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception):
    """Handle general exceptions."""
    correlation_id = getattr(request.state, 'correlation_id', None)
    
    logger.error(f"Unhandled exception: {exc}", exc_info=True)
    
    error_response = ErrorResponse(
        error_type="INTERNAL_SERVER_ERROR",
        message="An internal server error occurred",
        details={"exception_type": type(exc).__name__},
        correlation_id=correlation_id
    )
    
    return JSONResponse(
        status_code=500,
        content=error_response.dict()
    )


# Dependency to get correlation ID
def get_correlation_id_dependency(request: Request) -> str:
    """Get correlation ID from request state."""
    return getattr(request.state, 'correlation_id', str(uuid.uuid4()))


# API Endpoints

@app.post("/transcribe", response_model=TranscriptionResponse)
async def transcribe_audio(
    file: UploadFile = File(..., description="Audio file to transcribe"),
    model_size: str = Query(None, description="Model size to use"),
    language: str = Query(None, description="Language hint (ISO 639-1)"),
    temperature: float = Query(0.0, ge=0.0, le=1.0, description="Temperature for sampling"),
    task: str = Query("transcribe", description="Task: transcribe or translate"),
    correlation_id: str = Depends(get_correlation_id_dependency)
):
    """
    Transcribe an audio file using Whisper.
    
    Supports multiple audio formats and provides detailed transcription results
    with timing information and confidence scores.
    """
    start_time = time.time()
    
    try:
        # Validate file
        if not file.filename:
            raise HTTPException(status_code=400, detail="No file provided")
        
        # Create safe filename
        safe_name = safe_filename(file.filename)
        logger.info(f"Processing file: {safe_name} [{correlation_id}]")
        
        # Validate file size
        if file.size and file.size > settings.max_file_size_bytes:
            raise HTTPException(
                status_code=413,
                detail=f"File too large. Maximum size: {settings.max_file_size_mb}MB"
            )
        
        # Create temporary file
        import tempfile
        import os
        
        with tempfile.NamedTemporaryFile(delete=False, suffix=f".{safe_name.split('.')[-1]}") as temp_file:
            # Write uploaded file to temporary location
            content = await file.read()
            temp_file.write(content)
            temp_file_path = temp_file.name
        
        try:
            # Transcribe audio
            result = await whisper_service.transcribe_audio(
                file_path=temp_file_path,
                model_size=model_size,
                language=language,
                temperature=temperature,
                task=task
            )
            
            # Log performance metrics
            duration = time.time() - start_time
            log_performance_metrics(
                operation="api_transcription",
                duration=duration,
                file_size=file.size,
                memory_usage=get_memory_usage(),
                correlation_id=correlation_id,
                model_size=model_size or whisper_service.get_current_model_size()
            )
            
            return result
        
        finally:
            # Cleanup temporary file
            try:
                os.unlink(temp_file_path)
            except Exception as e:
                logger.warning(f"Failed to cleanup temp file {temp_file_path}: {e}")
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Transcription failed [{correlation_id}]: {e}")
        raise HTTPException(status_code=500, detail="Transcription failed")


@app.get("/health", response_model=HealthResponse)
async def health_check():
    """Health check endpoint."""
    return HealthResponse(
        status="healthy" if whisper_service.is_model_loaded() else "degraded",
        model_loaded=whisper_service.is_model_loaded(),
        model_size=whisper_service.get_current_model_size(),
        uptime=round(whisper_service.get_uptime(), 2)
    )


@app.get("/model/info", response_model=ModelInfoResponse)
async def get_model_info():
    """Get information about the currently loaded model."""
    return whisper_service.get_model_info()


@app.post("/model/load/{model_size}")
async def load_model(model_size: str):
    """Load a specific Whisper model size."""
    try:
        # Validate model size
        if model_size not in settings.available_model_sizes:
            raise HTTPException(
                status_code=400,
                detail=f"Invalid model size. Available: {settings.available_model_sizes}"
            )
        
        # Load the model
        result = await whisper_service.load_model(model_size)
        return {
            "success": True,
            "model_size": model_size,
            "message": f"Model {model_size} loaded successfully"
        }
    
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Failed to load model {model_size}: {e}")
        raise HTTPException(status_code=500, detail=f"Failed to load model: {str(e)}")






if __name__ == "__main__":
    # Run the application
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level=settings.log_level.lower()
    )
