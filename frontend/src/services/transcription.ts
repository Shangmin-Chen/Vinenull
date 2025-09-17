// Transcription API Service

import apiClient from './api';
import {
  AudioUploadResponse,
  TranscriptionStatusResponse,
  TranscriptionResultResponse
} from '../types/transcription';

export class TranscriptionService {
  /**
   * Upload audio file for transcription
   */
  static async uploadAudio(file: File): Promise<AudioUploadResponse> {
    const formData = new FormData();
    formData.append('audioFile', file);

    const response = await apiClient.post<AudioUploadResponse>('/audio/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    return response.data;
  }

  /**
   * Get transcription status
   */
  static async getStatus(jobId: string): Promise<TranscriptionStatusResponse> {
    const response = await apiClient.get<TranscriptionStatusResponse>(`/audio/status/${jobId}`);
    return response.data;
  }

  /**
   * Get transcription result
   */
  static async getResult(jobId: string): Promise<TranscriptionResultResponse> {
    const response = await apiClient.get<TranscriptionResultResponse>(`/audio/result/${jobId}`);
    return response.data;
  }

  /**
   * Health check
   */
  static async healthCheck(): Promise<{ status: string }> {
    const response = await apiClient.get('/audio/health');
    return response.data;
  }
}

export default TranscriptionService;
