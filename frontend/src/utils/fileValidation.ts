// File Validation Utilities

import { APP_CONFIG, ERROR_MESSAGES } from './constants';

export interface ValidationResult {
  isValid: boolean;
  error?: string;
}

/**
 * Validate audio file before upload
 */
export const validateAudioFile = (file: File): ValidationResult => {
  // Check if file exists
  if (!file) {
    return {
      isValid: false,
      error: ERROR_MESSAGES.FILE_REQUIRED,
    };
  }

  // Check file size
  if (file.size > APP_CONFIG.maxFileSize) {
    return {
      isValid: false,
      error: ERROR_MESSAGES.FILE_TOO_LARGE,
    };
  }

  // Check file extension first (more reliable)
  const extension = getFileExtension(file.name);
  if (!APP_CONFIG.supportedExtensions.includes(extension)) {
    return {
      isValid: false,
      error: ERROR_MESSAGES.INVALID_FORMAT,
    };
  }

  // Check file type (MIME type) - be more permissive
  // If MIME type is not in our list but extension is valid, still allow it
  // This handles cases where browsers report different MIME types
  if (file.type && !file.type.startsWith('audio/') && !APP_CONFIG.supportedFormats.includes(file.type)) {
    // Only reject if it's clearly not an audio file and not in our supported formats
    if (!file.type.includes('audio') && !APP_CONFIG.supportedFormats.includes(file.type)) {
      return {
        isValid: false,
        error: ERROR_MESSAGES.INVALID_FORMAT,
      };
    }
  }

  return { isValid: true };
};

/**
 * Get file extension from filename
 */
export const getFileExtension = (filename: string): string => {
  return filename.toLowerCase().substring(filename.lastIndexOf('.'));
};

/**
 * Format file size for display
 */
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes';

  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

/**
 * Get file type display name
 */
export const getFileTypeDisplayName = (mimeType: string): string => {
  const typeMap: Record<string, string> = {
    'audio/mpeg': 'MP3',
    'audio/wav': 'WAV',
    'audio/mp4': 'M4A',
    'audio/flac': 'FLAC',
    'audio/ogg': 'OGG',
    'audio/x-ms-wma': 'WMA',
  };

  return typeMap[mimeType] || mimeType;
};

/**
 * Check if file is audio
 */
export const isAudioFile = (file: File): boolean => {
  return file.type.startsWith('audio/') || 
         APP_CONFIG.supportedExtensions.some(ext => 
           file.name.toLowerCase().endsWith(ext)
         );
};
