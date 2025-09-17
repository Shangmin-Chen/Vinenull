// Results Page Component

import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { TranscriptionService } from '../services/transcription';
import { ResultsView } from '../components/results/ResultsView';
import { Loading } from '../components/common/Loading';
import { Button } from '../components/common/Button';
import { ArrowLeft, RefreshCw, AlertCircle } from 'lucide-react';

export const ResultsPage: React.FC = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const navigate = useNavigate();

  const {
    data: result,
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ['transcription-result', jobId],
    queryFn: () => TranscriptionService.getResult(jobId!),
    enabled: !!jobId,
  });

  if (!jobId) {
    return (
      <div className="text-center">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
          Invalid Job ID
        </h1>
        <p className="text-gray-600 dark:text-gray-300 mb-6">
          The job ID provided is invalid or missing.
        </p>
        <Button onClick={() => navigate('/')}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Home
        </Button>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto">
        <div className="card p-8">
          <Loading text="Loading results..." />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="card p-8 text-center">
          <div className="flex justify-center mb-4">
            <AlertCircle className="h-12 w-12 text-red-500" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Error Loading Results
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            {error.message || 'Failed to load transcription results'}
          </p>
          <div className="flex gap-3 justify-center">
            <Button onClick={() => refetch()}>
              <RefreshCw className="h-4 w-4 mr-2" />
              Try Again
            </Button>
            <Button variant="secondary" onClick={() => navigate('/')}>
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Home
            </Button>
          </div>
        </div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="card p-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Results Not Found
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            The transcription results could not be found. The job may still be processing.
          </p>
          <div className="flex gap-3 justify-center">
            <Button onClick={() => navigate(`/status/${jobId}`)}>
              Check Status
            </Button>
            <Button variant="secondary" onClick={() => navigate('/')}>
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Home
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Check if transcription failed
  if (result.status === 'FAILED') {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="card p-8 text-center">
          <div className="flex justify-center mb-4">
            <AlertCircle className="h-12 w-12 text-red-500" />
          </div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Transcription Failed
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            {result.message || 'The transcription process failed. Please try again with a different file.'}
          </p>
          <div className="flex gap-3 justify-center">
            <Button onClick={() => navigate('/')}>
              Try Another File
            </Button>
            <Button variant="secondary" onClick={() => navigate(`/status/${jobId}`)}>
              View Status
            </Button>
          </div>
        </div>
      </div>
    );
  }

  // Check if transcription is not completed yet
  if (result.status !== 'COMPLETED') {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="card p-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Transcription In Progress
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            Your transcription is still being processed. Please check back in a moment.
          </p>
          <div className="flex gap-3 justify-center">
            <Button onClick={() => navigate(`/status/${jobId}`)}>
              Check Status
            </Button>
            <Button variant="secondary" onClick={() => navigate('/')}>
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Home
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          Transcription Results
        </h1>
        <p className="text-gray-600 dark:text-gray-300">
          Your audio has been successfully transcribed
        </p>
      </div>

      {/* Results */}
      <ResultsView result={result} />

      {/* Actions */}
      <div className="max-w-4xl mx-auto">
        <div className="flex gap-3 justify-center">
          <Button
            variant="outline"
            onClick={() => refetch()}
          >
            <RefreshCw className="h-4 w-4 mr-2" />
            Refresh Results
          </Button>
          
          <Button
            variant="secondary"
            onClick={() => navigate('/')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Transcribe Another File
          </Button>
        </div>
      </div>
    </div>
  );
};
