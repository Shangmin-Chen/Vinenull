// Results View Component

import React, { useState } from 'react';
import { 
  Copy, 
  Download, 
  Volume2, 
  Clock,
  FileText,
  CheckCircle
} from 'lucide-react';
import { clsx } from 'clsx';
import { 
  TranscriptionResultResponse
} from '../../types/transcription';
import { 
  formatDuration, 
  formatTimestamp, 
  formatConfidence,
  formatProcessingTime,
  formatFileSizeMB
} from '../../utils/formatters';
import { Button } from '../common/Button';

interface ResultsViewProps {
  result: TranscriptionResultResponse;
  className?: string;
}

export const ResultsView: React.FC<ResultsViewProps> = ({
  result,
  className,
}) => {
  const [copied, setCopied] = useState(false);
  const [selectedSegment, setSelectedSegment] = useState<number | null>(null);

  if (!result.result) {
    return (
      <div className={clsx('card p-6 text-center', className)}>
        <p className="text-gray-500 dark:text-gray-400">
          No transcription result available
        </p>
      </div>
    );
  }

  const { result: transcription } = result;

  const handleCopyText = async () => {
    try {
      await navigator.clipboard.writeText(transcription.text);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (error) {
      console.error('Failed to copy text:', error);
    }
  };

  const handleDownloadText = () => {
    const blob = new Blob([transcription.text], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `transcription-${result.jobId}.txt`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleDownloadJSON = () => {
    const data = {
      jobId: result.jobId,
      timestamp: result.timestamp,
      result: transcription,
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `transcription-${result.jobId}.json`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  return (
    <div className={clsx('space-y-6', className)}>
      {/* Header */}
      <div className="card p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-3">
            <CheckCircle className="h-8 w-8 text-green-600 dark:text-green-400" />
            <div>
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
                Transcription Complete
              </h2>
              <p className="text-gray-600 dark:text-gray-300">
                Your audio has been successfully transcribed
              </p>
            </div>
          </div>
          
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={handleCopyText}
              leftIcon={copied ? <CheckCircle className="h-4 w-4" /> : <Copy className="h-4 w-4" />}
            >
              {copied ? 'Copied!' : 'Copy'}
            </Button>
            
            <Button
              variant="outline"
              size="sm"
              onClick={handleDownloadText}
              leftIcon={<Download className="h-4 w-4" />}
            >
              Download TXT
            </Button>
            
            <Button
              variant="outline"
              size="sm"
              onClick={handleDownloadJSON}
              leftIcon={<FileText className="h-4 w-4" />}
            >
              Download JSON
            </Button>
          </div>
        </div>

        {/* Metadata */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="flex items-center gap-2 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <Clock className="h-4 w-4 text-gray-500 dark:text-gray-400" />
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400">Duration</p>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                {formatDuration(transcription.duration)}
              </p>
            </div>
          </div>
          
          <div className="flex items-center gap-2 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <Volume2 className="h-4 w-4 text-gray-500 dark:text-gray-400" />
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400">Language</p>
              <p className="text-sm font-medium text-gray-900 dark:text-white capitalize">
                {transcription.language}
              </p>
            </div>
          </div>
          
          <div className="flex items-center gap-2 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <FileText className="h-4 w-4 text-gray-500 dark:text-gray-400" />
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400">File Size</p>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                {formatFileSizeMB(transcription.fileSizeMb * 1024 * 1024)}
              </p>
            </div>
          </div>
          
          <div className="flex items-center gap-2 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <Clock className="h-4 w-4 text-gray-500 dark:text-gray-400" />
            <div>
              <p className="text-xs text-gray-500 dark:text-gray-400">Processing Time</p>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                {formatProcessingTime(transcription.processingTime)}
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Transcription Text */}
      <div className="card p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white">
            Transcription
          </h3>
          {transcription.confidenceScore && (
            <div className="flex items-center gap-2">
              <span className="text-sm text-gray-500 dark:text-gray-400">Confidence:</span>
              <span className="text-sm font-medium text-gray-900 dark:text-white">
                {formatConfidence(transcription.confidenceScore)}
              </span>
            </div>
          )}
        </div>
        
        <div className="prose prose-gray dark:prose-invert max-w-none">
          <p className="text-gray-900 dark:text-white leading-relaxed whitespace-pre-wrap">
            {transcription.text}
          </p>
        </div>
      </div>

      {/* Segments */}
      {transcription.segments && transcription.segments.length > 0 && (
        <div className="card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Segments
          </h3>
          
          <div className="space-y-3">
            {transcription.segments.map((segment, index) => (
              <div
                key={index}
                className={clsx(
                  'p-4 rounded-lg border transition-colors cursor-pointer',
                  selectedSegment === index
                    ? 'border-blue-300 bg-blue-50 dark:border-blue-600 dark:bg-blue-900/20'
                    : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
                )}
                onClick={() => setSelectedSegment(selectedSegment === index ? null : index)}
              >
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <span className="text-xs font-mono text-gray-500 dark:text-gray-400">
                        {formatDuration(segment.startTime)} - {formatDuration(segment.endTime)}
                      </span>
                      {segment.confidence && (
                        <span className="text-xs text-gray-500 dark:text-gray-400">
                          ({formatConfidence(segment.confidence)})
                        </span>
                      )}
                    </div>
                    <p className="text-gray-900 dark:text-white">
                      {segment.text}
                    </p>
                  </div>
                  
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={(e) => {
                      e.stopPropagation();
                      navigator.clipboard.writeText(segment.text);
                    }}
                  >
                    <Copy className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Model Information */}
      <div className="card p-6">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
          Model Information
        </h3>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Model Used</p>
            <p className="text-sm font-medium text-gray-900 dark:text-white">
              {transcription.modelUsed}
            </p>
          </div>
          
          <div>
            <p className="text-sm text-gray-500 dark:text-gray-400">Completed</p>
            <p className="text-sm font-medium text-gray-900 dark:text-white">
              {formatTimestamp(result.timestamp)}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};
