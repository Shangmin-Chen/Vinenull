// Custom hook for polling status updates

import { useEffect, useRef, useCallback } from 'react';
import { TranscriptionService } from '../services/transcription';
import { TranscriptionStatus, TranscriptionStatusResponse } from '../types/transcription';
import { APP_CONFIG } from '../utils/constants';

export interface UsePollingOptions {
  enabled: boolean;
  onStatusUpdate?: (status: TranscriptionStatusResponse) => void;
  onComplete?: (status: TranscriptionStatusResponse) => void;
  onError?: (error: Error) => void;
}

export interface UsePollingReturn {
  startPolling: () => void;
  stopPolling: () => void;
  isPolling: boolean;
}

export const usePolling = (
  jobId: string | null,
  options: UsePollingOptions
): UsePollingReturn => {
  const intervalRef = useRef<NodeJS.Timeout | null>(null);
  const isPollingRef = useRef(false);
  const attemptCountRef = useRef(0);

  const { enabled, onStatusUpdate, onComplete, onError } = options;

  const pollStatus = useCallback(async () => {
    if (!jobId || !enabled) return;

    try {
      const status = await TranscriptionService.getStatus(jobId);
      attemptCountRef.current += 1;

      onStatusUpdate?.(status);

      // Check if transcription is complete
      if (status.status === TranscriptionStatus.COMPLETED || 
          status.status === TranscriptionStatus.FAILED) {
        onComplete?.(status);
        stopPolling();
        return;
      }

      // Stop polling after max attempts
      if (attemptCountRef.current >= APP_CONFIG.maxPollingAttempts) {
        onError?.(new Error('Polling timeout - maximum attempts reached'));
        stopPolling();
        return;
      }

    } catch (error) {
      console.error('Polling error:', error);
      onError?.(error as Error);
    }
  }, [jobId, enabled, onStatusUpdate, onComplete, onError]);

  const startPolling = useCallback(() => {
    if (isPollingRef.current || !jobId || !enabled) return;

    isPollingRef.current = true;
    attemptCountRef.current = 0;

    // Poll immediately
    pollStatus();

    // Set up interval
    intervalRef.current = setInterval(pollStatus, APP_CONFIG.pollingInterval);
  }, [pollStatus, jobId, enabled]);

  const stopPolling = useCallback(() => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
    isPollingRef.current = false;
    attemptCountRef.current = 0;
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopPolling();
    };
  }, [stopPolling]);

  // Auto-start/stop based on enabled state
  useEffect(() => {
    if (enabled && jobId) {
      startPolling();
    } else {
      stopPolling();
    }
  }, [enabled, jobId, startPolling, stopPolling]);

  return {
    startPolling,
    stopPolling,
    isPolling: isPollingRef.current,
  };
};
