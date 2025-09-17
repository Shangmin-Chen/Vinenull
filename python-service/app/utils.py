"""
Utility functions for file handling, audio processing, and other common operations.
"""

import os
import uuid
import logging
from pathlib import Path
from typing import Optional, Tuple, Dict, Any
import librosa
import soundfile as sf

from .config import settings
from .exceptions import (
    InvalidAudioFormat,
    FileTooLarge,
    AudioProcessingError,
    FileSystemError
)

logger = logging.getLogger(__name__)


def get_file_extension(filename: str) -> str:
    """Extract file extension from filename."""
    return Path(filename).suffix.lower().lstrip('.')


def validate_file_format(filename: str) -> bool:
    """Validate if file format is supported."""
    extension = get_file_extension(filename)
    return extension in settings.supported_formats_set


def validate_file_size(file_size: int) -> bool:
    """Validate if file size is within limits."""
    return file_size <= settings.max_file_size_bytes


def detect_audio_format(file_path: str) -> Optional[str]:
    """Detect audio format using file signature."""
    try:
        # Read first few bytes to detect format
        with open(file_path, 'rb') as f:
            header = f.read(12)
        
        # Check for common audio format signatures
        if header.startswith(b'ID3') or header[1:4] == b'ID3':
            return 'mp3'
        elif header.startswith(b'RIFF') and header[8:12] == b'WAVE':
            return 'wav'
        elif header.startswith(b'OggS'):
            return 'ogg'
        elif header.startswith(b'fLaC'):
            return 'flac'
        elif header.startswith(b'\x00\x00\x00\x20ftypM4A'):
            return 'm4a'
        elif header.startswith(b'\x30\x26\xB2\x75'):
            return 'wma'
        
        # Fallback to extension-based detection
        return get_file_extension(file_path)
    
    except Exception as e:
        logger.warning(f"Failed to detect audio format for {file_path}: {e}")
        return get_file_extension(file_path)


def create_temp_file(suffix: str = None) -> Tuple[str, str]:
    """Create a temporary file and return path and filename."""
    try:
        # Create unique filename
        unique_id = str(uuid.uuid4())
        if suffix:
            filename = f"whisperrr_{unique_id}.{suffix}"
        else:
            filename = f"whisperrr_{unique_id}"
        
        file_path = os.path.join(settings.upload_dir, filename)
        
        # Ensure directory exists
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        
        return file_path, filename
    
    except Exception as e:
        raise FileSystemError(
            message="Failed to create temporary file",
            operation="create_temp_file",
            original_error=str(e)
        )


def cleanup_temp_file(file_path: str) -> None:
    """Safely cleanup temporary file."""
    try:
        if os.path.exists(file_path):
            os.remove(file_path)
            logger.debug(f"Cleaned up temporary file: {file_path}")
    except Exception as e:
        logger.warning(f"Failed to cleanup temporary file {file_path}: {e}")


def get_audio_info(file_path: str) -> Dict[str, Any]:
    """Get audio file information."""
    try:
        # Use librosa to get audio info
        duration = librosa.get_duration(path=file_path)
        sr = librosa.get_samplerate(file_path)
        
        # Get file size
        file_size = os.path.getsize(file_path)
        
        return {
            "duration": duration,
            "sample_rate": sr,
            "file_size": file_size,
            "file_size_mb": round(file_size / (1024 * 1024), 2)
        }
    
    except Exception as e:
        raise AudioProcessingError(
            message="Failed to get audio information",
            original_error=str(e),
            processing_step="get_audio_info"
        )


def preprocess_audio(file_path: str, target_sr: int = 16000) -> str:
    """
    Preprocess audio file for Whisper.
    
    Args:
        file_path: Path to input audio file
        target_sr: Target sample rate (Whisper expects 16kHz)
    
    Returns:
        Path to preprocessed audio file
    """
    try:
        logger.info(f"Preprocessing audio file: {file_path}")
        
        # Get audio info
        audio_info = get_audio_info(file_path)
        logger.debug(f"Original audio info: {audio_info}")
        
        # Load audio
        audio, sr = librosa.load(file_path, sr=None)
        
        # Resample if necessary
        if sr != target_sr:
            logger.info(f"Resampling from {sr}Hz to {target_sr}Hz")
            audio = librosa.resample(audio, orig_sr=sr, target_sr=target_sr)
            sr = target_sr
        
        # Normalize audio
        audio = librosa.util.normalize(audio)
        
        # Create output file
        output_path, _ = create_temp_file("wav")
        
        # Save as WAV (Whisper prefers WAV format)
        sf.write(output_path, audio, sr, format='WAV', subtype='PCM_16')
        
        logger.info(f"Audio preprocessed successfully: {output_path}")
        return output_path
    
    except Exception as e:
        raise AudioProcessingError(
            message="Failed to preprocess audio",
            original_error=str(e),
            processing_step="preprocess_audio"
        )


def validate_audio_file(file_path: str) -> Dict[str, Any]:
    """
    Comprehensive audio file validation.
    
    Args:
        file_path: Path to audio file
    
    Returns:
        Dictionary with validation results and file info
    """
    try:
        # Check if file exists
        if not os.path.exists(file_path):
            raise FileSystemError(
                message="File does not exist",
                file_path=file_path
            )
        
        # Get file size
        file_size = os.path.getsize(file_path)
        
        # Validate file size
        if not validate_file_size(file_size):
            raise FileTooLarge(
                file_size=file_size,
                max_size=settings.max_file_size_bytes
            )
        
        # Detect format
        detected_format = detect_audio_format(file_path)
        
        # Validate format
        if not validate_file_format(file_path):
            raise InvalidAudioFormat(
                file_format=detected_format,
                supported_formats=settings.supported_formats
            )
        
        # Try to get audio info (this will fail if file is corrupted)
        audio_info = get_audio_info(file_path)
        
        return {
            "valid": True,
            "format": detected_format,
            "file_size": file_size,
            "file_size_mb": round(file_size / (1024 * 1024), 2),
            "duration": audio_info["duration"],
            "sample_rate": audio_info["sample_rate"]
        }
    
    except (InvalidAudioFormat, FileTooLarge, FileSystemError):
        # Re-raise our custom exceptions
        raise
    except Exception as e:
        raise AudioProcessingError(
            message="Audio file validation failed",
            original_error=str(e),
            processing_step="validate_audio_file"
        )


def get_memory_usage() -> float:
    """Get current memory usage in MB."""
    try:
        import psutil
        process = psutil.Process()
        memory_info = process.memory_info()
        return round(memory_info.rss / (1024 * 1024), 2)
    except ImportError:
        # psutil not available, return 0
        return 0.0
    except Exception:
        return 0.0




def safe_filename(filename: str) -> str:
    """Create a safe filename by removing/replacing unsafe characters."""
    import re
    
    # Remove or replace unsafe characters
    safe_name = re.sub(r'[^\w\-_\.]', '_', filename)
    
    # Remove multiple consecutive underscores
    safe_name = re.sub(r'_+', '_', safe_name)
    
    # Remove leading/trailing underscores and dots
    safe_name = safe_name.strip('_.')
    
    # Ensure it's not empty
    if not safe_name:
        safe_name = "audio_file"
    
    return safe_name


def get_correlation_id() -> str:
    """Generate a unique correlation ID for request tracking."""
    return str(uuid.uuid4())


def log_performance_metrics(
    operation: str,
    duration: float,
    file_size: Optional[int] = None,
    memory_usage: Optional[float] = None,
    **kwargs
) -> None:
    """Log performance metrics for monitoring."""
    metrics = {
        "operation": operation,
        "duration_seconds": round(duration, 3)
    }
    
    if file_size:
        metrics["file_size_mb"] = round(file_size / (1024 * 1024), 2)
    
    if memory_usage:
        metrics["memory_usage_mb"] = memory_usage
    
    # Add any additional metrics
    metrics.update(kwargs)
    
    logger.info(f"Performance metrics: {metrics}")


