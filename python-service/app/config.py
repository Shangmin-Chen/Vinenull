"""
Configuration management for the Whisperrr FastAPI service.
"""

import os
from typing import List, Union
from pydantic import validator, field_validator
from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """Application settings with environment variable support."""
    
    # Model configuration
    model_size: str = "base"
    max_file_size_mb: int = 25
    upload_dir: str = "/tmp/whisperrr_uploads"
    log_level: str = "INFO"
    
    # API configuration
    api_title: str = "Whisperrr Transcription Service"
    api_description: str = "Production-ready audio transcription using OpenAI Whisper"
    api_version: str = "1.0.0"
    cors_origins: Union[List[str], str] = ["http://localhost:8080", "http://localhost:3000", "http://127.0.0.1:8080", "http://127.0.0.1:3000"]
    
    # Processing configuration
    max_concurrent_transcriptions: int = 3
    request_timeout_seconds: int = 300
    cleanup_temp_files: bool = True
    
    # Performance and monitoring
    enable_metrics: bool = True
    enable_health_checks: bool = True
    
    # Supported audio formats
    supported_formats: List[str] = ["mp3", "wav", "m4a", "flac", "ogg", "wma"]
    
    # Model size options
    available_model_sizes: List[str] = ["tiny", "base", "small", "medium", "large"]
    
    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = False
    
    @validator("model_size")
    def validate_model_size(cls, v):
        """Validate model size is supported."""
        valid_sizes = ["tiny", "base", "small", "medium", "large"]
        if v not in valid_sizes:
            raise ValueError(f"Model size must be one of: {valid_sizes}")
        return v
    
    @validator("log_level")
    def validate_log_level(cls, v):
        """Validate log level."""
        valid_levels = ["DEBUG", "INFO", "WARNING", "ERROR", "CRITICAL"]
        if v.upper() not in valid_levels:
            raise ValueError(f"Log level must be one of: {valid_levels}")
        return v.upper()
    
    @validator("max_file_size_mb")
    def validate_max_file_size(cls, v):
        """Validate max file size is reasonable."""
        if v <= 0 or v > 1000:  # Max 1GB
            raise ValueError("Max file size must be between 1 and 1000 MB")
        return v
    
    @validator("upload_dir")
    def validate_upload_dir(cls, v):
        """Ensure upload directory exists."""
        os.makedirs(v, exist_ok=True)
        return v
    
    @validator("cors_origins", pre=True)
    def parse_cors_origins(cls, v):
        """Parse CORS origins from string or list."""
        if isinstance(v, str):
            # Handle comma-separated string from environment variables
            return [origin.strip() for origin in v.split(",") if origin.strip()]
        return v
    
    @property
    def max_file_size_bytes(self) -> int:
        """Get max file size in bytes."""
        return self.max_file_size_mb * 1024 * 1024
    
    @property
    def supported_formats_set(self) -> set:
        """Get supported formats as a set for faster lookup."""
        return set(self.supported_formats)


# Global settings instance
settings = Settings()
