// Application Constants

export const APP_CONFIG = {
  name: 'Whisperrr',
  version: '1.0.0',
  description: 'AI-powered audio transcription platform',
  maxFileSize: 25 * 1024 * 1024, // 25MB in bytes
  supportedFormats: [
    // MP3 variants
    'audio/mpeg',
    'audio/mp3',
    'audio/mpeg3',
    // WAV variants
    'audio/wav',
    'audio/wave',
    'audio/x-wav',
    // M4A variants
    'audio/mp4',
    'audio/m4a',
    'audio/x-m4a',
    // FLAC variants
    'audio/flac',
    'audio/x-flac',
    // OGG variants
    'audio/ogg',
    'audio/vorbis',
    // WMA variants
    'audio/x-ms-wma',
    'audio/wma',
  ],
  supportedExtensions: ['.mp3', '.wav', '.m4a', '.flac', '.ogg', '.wma'],
  pollingInterval: 2000, // 2 seconds
  maxPollingAttempts: 300, // 10 minutes max
};

export const ROUTES = {
  HOME: '/',
  STATUS: '/status',
  RESULTS: '/results',
} as const;

export const TRANSCRIPTION_STATUS = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  COMPLETED: 'COMPLETED',
  FAILED: 'FAILED',
} as const;

export const ERROR_MESSAGES = {
  FILE_TOO_LARGE: 'File size must be less than 25MB',
  INVALID_FORMAT: 'Unsupported audio format. Please use MP3, WAV, M4A, FLAC, OGG, or WMA',
  UPLOAD_FAILED: 'Failed to upload file. Please try again.',
  NETWORK_ERROR: 'Network error. Please check your connection.',
  UNKNOWN_ERROR: 'An unexpected error occurred. Please try again.',
  TRANSCRIPTION_FAILED: 'Transcription failed. Please try with a different file.',
  FILE_REQUIRED: 'Please select an audio file to upload',
} as const;

export const SUCCESS_MESSAGES = {
  UPLOAD_SUCCESS: 'File uploaded successfully!',
  TRANSCRIPTION_COMPLETE: 'Transcription completed successfully!',
} as const;
