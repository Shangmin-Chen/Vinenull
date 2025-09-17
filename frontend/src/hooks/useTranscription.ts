// Custom hook for managing transcription state

import { useState, useCallback } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { TranscriptionService } from '../services/transcription';
import { 
  AudioUploadResponse, 
  TranscriptionResultResponse,
  TranscriptionStatus 
} from '../types/transcription';
import { APP_CONFIG } from '../utils/constants';

export interface UseTranscriptionReturn {
  // State
  currentJobId: string | null;
  uploadStatus: 'idle' | 'uploading' | 'success' | 'error';
  transcriptionStatus: TranscriptionStatus | null;
  transcriptionResult: TranscriptionResultResponse | null;
  error: string | null;
  
  // Actions
  uploadAudio: (file: File) => Promise<void>;
  clearError: () => void;
  reset: () => void;
  
  // Computed
  isUploading: boolean;
  isProcessing: boolean;
  isCompleted: boolean;
  isFailed: boolean;
  progress: number;
}

export const useTranscription = (): UseTranscriptionReturn => {
  const [currentJobId, setCurrentJobId] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Upload mutation
  const uploadMutation = useMutation({
    mutationFn: (file: File) => TranscriptionService.uploadAudio(file),
    onSuccess: (data: AudioUploadResponse) => {
      setCurrentJobId(data.jobId);
      setError(null);
    },
    onError: (error: any) => {
      setError(error.response?.data?.message || error.message || 'Upload failed');
    },
  });

  // Status query (only runs when we have a jobId)
  const statusQuery = useQuery({
    queryKey: ['transcription-status', currentJobId],
    queryFn: () => TranscriptionService.getStatus(currentJobId!),
    enabled: !!currentJobId,
    refetchInterval: (query) => {
      // Stop polling if completed or failed
      const data = query.state.data;
      if (data?.status === TranscriptionStatus.COMPLETED || 
          data?.status === TranscriptionStatus.FAILED) {
        return false;
      }
      return APP_CONFIG.pollingInterval;
    },
    refetchIntervalInBackground: true,
  });

  // Result query (only runs when status is completed)
  const resultQuery = useQuery({
    queryKey: ['transcription-result', currentJobId],
    queryFn: () => TranscriptionService.getResult(currentJobId!),
    enabled: !!currentJobId && statusQuery.data?.status === TranscriptionStatus.COMPLETED,
  });

  // Actions
  const uploadAudio = useCallback(async (file: File) => {
    setError(null);
    await uploadMutation.mutateAsync(file);
  }, [uploadMutation]);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  const reset = useCallback(() => {
    setCurrentJobId(null);
    setError(null);
    uploadMutation.reset();
    // Note: React Query v5 doesn't have remove method, queries are automatically cleaned up
  }, [uploadMutation]);

  // Computed values
  const isUploading = uploadMutation.isPending;
  const isProcessing = statusQuery.data?.status === TranscriptionStatus.PROCESSING;
  const isCompleted = statusQuery.data?.status === TranscriptionStatus.COMPLETED;
  const isFailed = statusQuery.data?.status === TranscriptionStatus.FAILED;
  const progress = statusQuery.data?.progress || 0;

  const uploadStatus = uploadMutation.isPending ? 'uploading' : 
                      uploadMutation.isError ? 'error' : 
                      uploadMutation.isSuccess ? 'success' : 'idle';

  return {
    // State
    currentJobId,
    uploadStatus,
    transcriptionStatus: statusQuery.data?.status || null,
    transcriptionResult: resultQuery.data || null,
    error: error || uploadMutation.error?.message || statusQuery.error?.message || resultQuery.error?.message,
    
    // Actions
    uploadAudio,
    clearError,
    reset,
    
    // Computed
    isUploading,
    isProcessing,
    isCompleted,
    isFailed,
    progress,
  };
};
