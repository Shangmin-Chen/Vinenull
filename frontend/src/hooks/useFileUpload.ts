// Custom hook for file upload functionality

import { useState, useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { validateAudioFile, formatFileSize } from '../utils/fileValidation';
import { ERROR_MESSAGES } from '../utils/constants';

export interface FileUploadState {
  file: File | null;
  isDragActive: boolean;
  isDragReject: boolean;
  error: string | null;
  isValid: boolean;
}

export interface UseFileUploadOptions {
  onFileSelect?: (file: File) => void;
  onError?: (error: string) => void;
  maxFiles?: number;
  disabled?: boolean;
}

export interface UseFileUploadReturn extends FileUploadState {
  // Actions
  selectFile: (file: File) => void;
  clearFile: () => void;
  clearError: () => void;
  
  // Dropzone props
  getRootProps: () => any;
  getInputProps: () => any;
  
  // Computed
  fileSize: string;
  fileName: string;
  canUpload: boolean;
}

export const useFileUpload = (options: UseFileUploadOptions = {}): UseFileUploadReturn => {
  const { onFileSelect, onError, maxFiles = 1, disabled = false } = options;
  
  const [state, setState] = useState<FileUploadState>({
    file: null,
    isDragActive: false,
    isDragReject: false,
    error: null,
    isValid: false,
  });


  const validateAndSetFile = useCallback((file: File) => {
    const validation = validateAudioFile(file);
    
    setState(prev => ({
      ...prev,
      file: validation.isValid ? file : null,
      error: validation.error || null,
      isValid: validation.isValid,
    }));

    if (validation.isValid) {
      onFileSelect?.(file);
    } else {
      onError?.(validation.error || ERROR_MESSAGES.UNKNOWN_ERROR);
    }
  }, [onFileSelect, onError]);

  const onDrop = useCallback((acceptedFiles: File[], rejectedFiles: any[]) => {
    if (disabled) return;

    // Handle rejected files
    if (rejectedFiles.length > 0) {
      const rejection = rejectedFiles[0];
      let errorMessage: string = ERROR_MESSAGES.INVALID_FORMAT;

      if (rejection.errors.some((e: any) => e.code === 'file-too-large')) {
        errorMessage = ERROR_MESSAGES.FILE_TOO_LARGE;
      } else if (rejection.errors.some((e: any) => e.code === 'file-invalid-type')) {
        errorMessage = ERROR_MESSAGES.INVALID_FORMAT;
      }

      setState(prev => ({
        ...prev,
        error: errorMessage,
        isValid: false,
        file: null,
      }));
      
      onError?.(errorMessage);
      return;
    }

    // Handle accepted files
    if (acceptedFiles.length > 0) {
      validateAndSetFile(acceptedFiles[0]);
    }
  }, [disabled, validateAndSetFile, onError]);

  const onDragEnter = useCallback(() => {
    if (disabled) return;
    setState(prev => ({ ...prev, isDragActive: true }));
  }, [disabled]);

  const onDragLeave = useCallback(() => {
    setState(prev => ({ ...prev, isDragActive: false, isDragReject: false }));
  }, []);

  const onDragOver = useCallback((event: any) => {
    if (disabled) return;
    event.preventDefault();
  }, [disabled]);

  const onDropRejected = useCallback(() => {
    setState(prev => ({ 
      ...prev, 
      isDragReject: true, 
      error: ERROR_MESSAGES.INVALID_FORMAT 
    }));
    onError?.(ERROR_MESSAGES.INVALID_FORMAT);
  }, [onError]);

  const { getRootProps, getInputProps } = useDropzone({
    onDrop,
    onDragEnter,
    onDragLeave,
    onDragOver,
    onDropRejected,
    accept: {
      'audio/*': ['.mp3', '.wav', '.m4a', '.flac', '.ogg', '.wma'],
      // Also accept by extension directly
      '.mp3': ['audio/mpeg', 'audio/mp3'],
      '.wav': ['audio/wav', 'audio/wave', 'audio/x-wav'],
      '.m4a': ['audio/mp4', 'audio/m4a', 'audio/x-m4a'],
      '.flac': ['audio/flac', 'audio/x-flac'],
      '.ogg': ['audio/ogg', 'audio/vorbis'],
      '.wma': ['audio/x-ms-wma', 'audio/wma']
    },
    maxFiles,
    maxSize: 25 * 1024 * 1024, // 25MB
    disabled,
    noClick: disabled,
    noKeyboard: disabled,
  });

  const selectFile = useCallback((file: File) => {
    if (disabled) return;
    validateAndSetFile(file);
  }, [disabled, validateAndSetFile]);

  const clearFile = useCallback(() => {
    setState(prev => ({
      ...prev,
      file: null,
      error: null,
      isValid: false,
    }));
  }, []);

  const clearError = useCallback(() => {
    setState(prev => ({ ...prev, error: null }));
  }, []);

  // Computed values
  const fileSize = state.file ? formatFileSize(state.file.size) : '';
  const fileName = state.file?.name || '';
  const canUpload = state.isValid && !!state.file && !disabled;

  return {
    ...state,
    selectFile,
    clearFile,
    clearError,
    getRootProps,
    getInputProps,
    fileSize,
    fileName,
    canUpload,
  };
};
