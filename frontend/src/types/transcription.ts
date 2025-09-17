// Transcription Types

export interface AudioUploadResponse {
  jobId: string;
  status: TranscriptionStatus;
  message: string;
  timestamp: string;
}

export interface TranscriptionStatusResponse {
  jobId: string;
  status: TranscriptionStatus;
  progress?: number;
  message: string;
  timestamp: string;
}

export interface TranscriptionResultResponse {
  jobId: string;
  status: TranscriptionStatus;
  result?: TranscriptionResult;
  message: string;
  timestamp: string;
}

export interface TranscriptionResult {
  text: string;
  language: string;
  duration: number;
  segments: TranscriptionSegment[];
  confidenceScore?: number;
  modelUsed: string;
  processingTime: number;
  fileSizeMb: number;
}

export interface TranscriptionSegment {
  startTime: number;
  endTime: number;
  text: string;
  confidence?: number;
}

export enum TranscriptionStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

export interface AudioFile {
  id: string;
  filename: string;
  size: number;
  format: string;
  uploadedAt: string;
  status: TranscriptionStatus;
}
