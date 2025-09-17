// Status Page Component

import React, { useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { TranscriptionService } from '../services/transcription';
import { StatusDisplay, StatusTimeline } from '../components/status/StatusDisplay';
import { Loading } from '../components/common/Loading';
import { Button } from '../components/common/Button';
import { ArrowLeft, RefreshCw } from 'lucide-react';
import { TranscriptionStatus } from '../types/transcription';

export const StatusPage: React.FC = () => {
  const { jobId } = useParams<{ jobId: string }>();
  const navigate = useNavigate();

  const {
    data: status,
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ['transcription-status', jobId],
    queryFn: () => TranscriptionService.getStatus(jobId!),
    enabled: !!jobId,
    refetchInterval: (query) => {
      // Stop polling if completed or failed
      const data = query.state.data;
      if (data?.status === TranscriptionStatus.COMPLETED || 
          data?.status === TranscriptionStatus.FAILED) {
        return false;
      }
      return 2000; // Poll every 2 seconds
    },
    refetchIntervalInBackground: true,
  });

  useEffect(() => {
    if (status?.status === TranscriptionStatus.COMPLETED) {
      // Navigate to results page when completed
      navigate(`/results/${jobId}`);
    }
  }, [status?.status, jobId, navigate]);

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
      <div className="max-w-2xl mx-auto">
        <div className="card p-8">
          <Loading text="Loading status..." />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="card p-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Error Loading Status
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            {error.message || 'Failed to load transcription status'}
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

  if (!status) {
    return (
      <div className="max-w-2xl mx-auto">
        <div className="card p-8 text-center">
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
            Status Not Found
          </h1>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            The transcription job could not be found.
          </p>
          <Button onClick={() => navigate('/')}>
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Home
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          Transcription Status
        </h1>
        <p className="text-gray-600 dark:text-gray-300">
          Track the progress of your audio transcription
        </p>
      </div>

      {/* Status Display */}
      <div className="max-w-2xl mx-auto">
        <StatusDisplay status={status} />
      </div>

      {/* Timeline */}
      <div className="max-w-md mx-auto">
        <div className="card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Progress Timeline
          </h3>
          <StatusTimeline currentStatus={status.status} />
        </div>
      </div>

      {/* Actions */}
      <div className="max-w-2xl mx-auto">
        <div className="flex gap-3 justify-center">
          <Button
            variant="outline"
            onClick={() => refetch()}
            disabled={status.status === TranscriptionStatus.COMPLETED}
          >
            <RefreshCw className="h-4 w-4 mr-2" />
            Refresh Status
          </Button>
          
          <Button
            variant="secondary"
            onClick={() => navigate('/')}
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Home
          </Button>
        </div>
      </div>

      {/* Auto-refresh indicator */}
      {status.status !== TranscriptionStatus.COMPLETED && 
       status.status !== TranscriptionStatus.FAILED && (
        <div className="text-center">
          <p className="text-sm text-gray-500 dark:text-gray-400">
            Status updates automatically every 2 seconds
          </p>
        </div>
      )}
    </div>
  );
};
