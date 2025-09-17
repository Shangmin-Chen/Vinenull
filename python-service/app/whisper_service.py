"""
WhisperService class for managing OpenAI Whisper models and transcription.
"""

import asyncio
import logging
import time
import threading
from datetime import datetime
from typing import Optional, Dict, Any, List
from concurrent.futures import ThreadPoolExecutor
import whisper
import torch

from .config import settings
from .models import (
    TranscriptionResponse,
    TranscriptionSegment,
    ModelInfoResponse
)
from .exceptions import (
    ModelNotLoaded,
    ModelLoadFailed,
    TranscriptionFailed,
    AudioProcessingError
)
from .utils import (
    preprocess_audio,
    validate_audio_file,
    get_memory_usage,
    log_performance_metrics,
    cleanup_temp_file
)

logger = logging.getLogger(__name__)


class WhisperService:
    """
    Singleton service for managing Whisper models and transcription.
    
    This class handles model loading, caching, and transcription operations
    with proper resource management and error handling.
    """
    
    _instance = None
    _lock = threading.Lock()
    
    def __new__(cls):
        """Ensure singleton pattern."""
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)
        return cls._instance
    
    def __init__(self):
        """Initialize the WhisperService."""
        if hasattr(self, '_initialized'):
            return
        
        self._initialized = True
        self._model = None
        self._model_size = None
        self._model_load_time = None
        self._is_loading = False
        self._active_transcriptions = 0
        self._start_time = time.time()
        self._executor = ThreadPoolExecutor(max_workers=settings.max_concurrent_transcriptions)
        
        # Model descriptions
        self._model_descriptions = {
            "tiny": "Fastest, least accurate (39 MB)",
            "base": "Good balance of speed and accuracy (74 MB)",
            "small": "Better accuracy, slower (244 MB)",
            "medium": "Good accuracy, slower (769 MB)",
            "large": "Best accuracy, slowest (1550 MB)"
        }
        
        # Supported languages (Whisper supports 99 languages)
        self._supported_languages = [
            "en", "zh", "de", "es", "ru", "ko", "fr", "ja", "pt", "tr",
            "pl", "ca", "nl", "ar", "sv", "it", "id", "hi", "fi", "vi",
            "he", "uk", "el", "ms", "cs", "ro", "da", "hu", "ta", "no",
            "th", "ur", "hr", "bg", "lt", "la", "mi", "ml", "cy", "sk",
            "te", "fa", "lv", "bn", "sr", "az", "sl", "kn", "et", "mk",
            "br", "eu", "is", "hy", "ne", "mn", "bs", "kk", "sq", "sw",
            "gl", "mr", "pa", "si", "km", "sn", "yo", "so", "af", "oc",
            "ka", "be", "tg", "sd", "gu", "am", "yi", "lo", "uz", "fo",
            "ht", "ps", "tk", "nn", "mt", "sa", "lb", "my", "bo", "tl",
            "mg", "as", "tt", "haw", "ln", "ha", "ba", "jw", "su"
        ]
        
        logger.info("WhisperService initialized")
    
    async def load_model(self, model_size: str = None) -> dict:
        """
        Load a Whisper model.
        
        Args:
            model_size: Model size to load (defaults to configured size)
        
        Returns:
            ModelLoadResponse with load information
        """
        if model_size is None:
            model_size = settings.model_size
        
        # Check if model is already loaded
        if self._model is not None and self._model_size == model_size and not self._is_loading:
            return {
                "success": True,
                "model_size": model_size,
                "load_time_seconds": 0.0,
                "memory_usage_mb": get_memory_usage(),
                "message": f"Model {model_size} already loaded"
            }
        
        # Prevent concurrent loading
        if self._is_loading:
            raise ModelLoadFailed(
                message="Model is already being loaded",
                model_size=model_size
            )
        
        self._is_loading = True
        start_time = time.time()
        
        try:
            logger.info(f"Loading Whisper model: {model_size}")
            
            # Load model in thread pool to avoid blocking
            loop = asyncio.get_event_loop()
            self._model = await loop.run_in_executor(
                self._executor,
                self._load_model_sync,
                model_size
            )
            
            self._model_size = model_size
            self._model_load_time = time.time()
            load_time = time.time() - start_time
            
            logger.info(f"Model {model_size} loaded successfully in {load_time:.2f}s")
            
            return {
                "success": True,
                "model_size": model_size,
                "load_time_seconds": round(load_time, 3),
                "memory_usage_mb": get_memory_usage(),
                "message": f"Model {model_size} loaded successfully"
            }
        
        except Exception as e:
            logger.error(f"Failed to load model {model_size}: {e}")
            raise ModelLoadFailed(
                message=f"Failed to load model {model_size}",
                model_size=model_size,
                original_error=str(e)
            )
        
        finally:
            self._is_loading = False
    
    def _load_model_sync(self, model_size: str):
        """Synchronous model loading (runs in thread pool)."""
        try:
            # Check if CUDA is available
            device = "cuda" if torch.cuda.is_available() else "cpu"
            logger.info(f"Loading model on device: {device}")
            
            # Load the model
            model = whisper.load_model(model_size, device=device)
            
            # Log memory usage
            memory_usage = get_memory_usage()
            logger.info(f"Model loaded, memory usage: {memory_usage} MB")
            
            return model
        
        except Exception as e:
            logger.error(f"Error loading model {model_size}: {e}")
            raise
    
    async def transcribe_audio(
        self,
        file_path: str,
        model_size: Optional[str] = None,
        language: Optional[str] = None,
        temperature: float = 0.0,
        task: str = "transcribe"
    ) -> TranscriptionResponse:
        """
        Transcribe audio file using Whisper.
        
        Args:
            file_path: Path to audio file
            model_size: Model size to use (defaults to loaded model)
            language: Language hint (ISO 639-1 code)
            temperature: Temperature for sampling
            task: Task type ('transcribe' or 'translate')
        
        Returns:
            TranscriptionResponse with transcription results
        """
        if self._model is None:
            raise ModelNotLoaded("No model is currently loaded")
        
        if model_size and model_size != self._model_size:
            # Load different model if requested
            await self.load_model(model_size)
        
        start_time = time.time()
        self._active_transcriptions += 1
        
        try:
            logger.info(f"Starting transcription: {file_path}")
            
            # Validate audio file
            file_info = validate_audio_file(file_path)
            logger.debug(f"File validation passed: {file_info}")
            
            # Preprocess audio
            processed_file = None
            try:
                processed_file = preprocess_audio(file_path)
                
                # Run transcription in thread pool
                loop = asyncio.get_event_loop()
                result = await loop.run_in_executor(
                    self._executor,
                    self._transcribe_sync,
                    processed_file,
                    language,
                    temperature,
                    task
                )
                
                processing_time = time.time() - start_time
                
                # Convert result to response model
                response = self._create_transcription_response(
                    result, file_info, processing_time
                )
                
                # Log performance metrics
                log_performance_metrics(
                    operation="transcription",
                    duration=processing_time,
                    file_size=file_info["file_size"],
                    memory_usage=get_memory_usage(),
                    model_size=self._model_size,
                    language=language
                )
                
                logger.info(f"Transcription completed in {processing_time:.2f}s")
                return response
            
            finally:
                # Cleanup processed file
                if processed_file and settings.cleanup_temp_files:
                    cleanup_temp_file(processed_file)
        
        except Exception as e:
            logger.error(f"Transcription failed: {e}")
            raise TranscriptionFailed(
                message="Transcription failed",
                original_error=str(e),
                file_path=file_path
            )
        
        finally:
            self._active_transcriptions -= 1
    
    def _transcribe_sync(
        self,
        file_path: str,
        language: Optional[str],
        temperature: float,
        task: str
    ):
        """Synchronous transcription (runs in thread pool)."""
        try:
            # Prepare transcription options
            options = {
                "temperature": temperature,
                "task": task
            }
            
            if language:
                options["language"] = language
            
            # Run transcription
            result = self._model.transcribe(file_path, **options)
            
            return result
        
        except Exception as e:
            logger.error(f"Sync transcription failed: {e}")
            raise
    
    def _create_transcription_response(
        self,
        whisper_result: Dict[str, Any],
        file_info: Dict[str, Any],
        processing_time: float
    ) -> TranscriptionResponse:
        """Create TranscriptionResponse from Whisper result."""
        
        # Extract segments
        segments = []
        for segment in whisper_result.get("segments", []):
            segments.append(TranscriptionSegment(
                start_time=segment.get("start", 0.0),
                end_time=segment.get("end", 0.0),
                text=segment.get("text", "").strip(),
                confidence=None  # Whisper doesn't provide confidence scores
            ))
        
        # Calculate overall confidence (if available)
        confidence_score = None
        if "segments" in whisper_result and whisper_result["segments"]:
            # Use average of segment-level confidence if available
            confidences = [
                seg.get("avg_logprob", 0) for seg in whisper_result["segments"]
                if "avg_logprob" in seg
            ]
            if confidences:
                # Convert log probability to confidence score
                confidence_score = max(0, min(1, sum(confidences) / len(confidences) + 1))
        
        return TranscriptionResponse(
            text=whisper_result.get("text", "").strip(),
            language=whisper_result.get("language"),
            duration=file_info["duration"],
            segments=segments,
            confidence_score=confidence_score,
            model_used=self._model_size,
            processing_time=round(processing_time, 3)
        )
    
    def get_model_info(self) -> ModelInfoResponse:
        """Get information about the currently loaded model."""
        return ModelInfoResponse(
            model_size=self._model_size or "none",
            memory_usage_mb=get_memory_usage(),
            load_time_seconds=0.0 if not self._model_load_time else time.time() - self._model_load_time,
            supported_languages=self._supported_languages,
            is_loaded=self._model is not None,
            last_loaded=datetime.fromtimestamp(self._model_load_time) if self._model_load_time else None
        )
    
    
    def get_uptime(self) -> float:
        """Get service uptime in seconds."""
        return time.time() - self._start_time
    
    def get_active_transcriptions(self) -> int:
        """Get number of active transcriptions."""
        return self._active_transcriptions
    
    def is_model_loaded(self) -> bool:
        """Check if a model is currently loaded."""
        return self._model is not None
    
    def get_current_model_size(self) -> Optional[str]:
        """Get currently loaded model size."""
        return self._model_size
    
    async def cleanup(self):
        """Cleanup resources and shutdown executor."""
        try:
            logger.info("Cleaning up WhisperService resources")
            
            # Wait for active transcriptions to complete
            while self._active_transcriptions > 0:
                logger.info(f"Waiting for {self._active_transcriptions} active transcriptions to complete")
                await asyncio.sleep(1)
            
            # Shutdown executor
            self._executor.shutdown(wait=True)
            
            # Clear model from memory
            if self._model is not None:
                del self._model
                self._model = None
                self._model_size = None
            
            # Force garbage collection
            import gc
            gc.collect()
            
            logger.info("WhisperService cleanup completed")
        
        except Exception as e:
            logger.error(f"Error during cleanup: {e}")


# Global service instance
whisper_service = WhisperService()
