// Status Display Component

import React from 'react';
import { 
  Clock, 
  Loader2, 
  CheckCircle, 
  XCircle, 
  FileAudio,
  Calendar
} from 'lucide-react';
import { clsx } from 'clsx';
import { 
  TranscriptionStatus, 
  TranscriptionStatusResponse 
} from '../../types/transcription';
import { formatTimestamp } from '../../utils/formatters';
import { Loading } from '../common/Loading';

interface StatusDisplayProps {
  status: TranscriptionStatusResponse;
  className?: string;
}

const statusConfig = {
  [TranscriptionStatus.PENDING]: {
    icon: Clock,
    label: 'Pending',
    description: 'Your file is queued for processing',
    className: 'status-pending',
    iconColor: 'text-yellow-600 dark:text-yellow-400',
    animated: false,
  },
  [TranscriptionStatus.PROCESSING]: {
    icon: Loader2,
    label: 'Processing',
    description: 'Transcribing your audio file',
    className: 'status-processing',
    iconColor: 'text-blue-600 dark:text-blue-400',
    animated: true,
  },
  [TranscriptionStatus.COMPLETED]: {
    icon: CheckCircle,
    label: 'Completed',
    description: 'Transcription finished successfully',
    className: 'status-completed',
    iconColor: 'text-green-600 dark:text-green-400',
    animated: false,
  },
  [TranscriptionStatus.FAILED]: {
    icon: XCircle,
    label: 'Failed',
    description: 'Transcription failed to complete',
    className: 'status-failed',
    iconColor: 'text-red-600 dark:text-red-400',
    animated: false,
  },
};

export const StatusDisplay: React.FC<StatusDisplayProps> = ({
  status,
  className,
}) => {
  const config = statusConfig[status.status];
  const Icon = config.icon;

  return (
    <div className={clsx('card p-6', className)}>
      <div className="space-y-6">
        {/* Status Header */}
        <div className="text-center">
          <div className="flex justify-center mb-4">
            <div className={clsx(
              'p-4 rounded-full',
              config.className
            )}>
              <Icon 
                className={clsx(
                  'h-8 w-8',
                  config.iconColor,
                  config.animated && 'animate-spin'
                )} 
              />
            </div>
          </div>
          
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
            {config.label}
          </h2>
          
          <p className="text-gray-600 dark:text-gray-300">
            {config.description}
          </p>
        </div>

        {/* Progress Bar */}
        {status.status === TranscriptionStatus.PROCESSING && status.progress !== undefined && (
          <div className="space-y-2">
            <div className="flex justify-between text-sm text-gray-600 dark:text-gray-300">
              <span>Progress</span>
              <span>{Math.round(status.progress)}%</span>
            </div>
            <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
              <div 
                className="bg-blue-600 h-2 rounded-full transition-all duration-300 ease-out"
                style={{ width: `${status.progress}%` }}
              />
            </div>
          </div>
        )}

        {/* Status Details */}
        <div className="space-y-4">
          <div className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <FileAudio className="h-5 w-5 text-gray-500 dark:text-gray-400" />
            <div>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                Job ID
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-300 font-mono">
                {status.jobId}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-gray-800 rounded-lg">
            <Calendar className="h-5 w-5 text-gray-500 dark:text-gray-400" />
            <div>
              <p className="text-sm font-medium text-gray-900 dark:text-white">
                Started
              </p>
              <p className="text-sm text-gray-600 dark:text-gray-300">
                {formatTimestamp(status.timestamp)}
              </p>
            </div>
          </div>

          {status.message && (
            <div className="p-3 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg">
              <p className="text-sm text-blue-700 dark:text-blue-300">
                {status.message}
              </p>
            </div>
          )}
        </div>

        {/* Processing Animation */}
        {status.status === TranscriptionStatus.PROCESSING && (
          <div className="text-center">
            <Loading text="Processing your audio..." />
          </div>
        )}
      </div>
    </div>
  );
};

// Status Badge Component
export const StatusBadge: React.FC<{
  status: TranscriptionStatus;
  className?: string;
}> = ({ status, className }) => {
  const config = statusConfig[status];
  const Icon = config.icon;

  return (
    <span className={clsx(
      'inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium',
      config.className,
      className
    )}>
      <Icon 
        className={clsx(
          'h-3 w-3',
          config.iconColor,
          config.animated && 'animate-spin'
        )} 
      />
      {config.label}
    </span>
  );
};

// Status Timeline Component
export const StatusTimeline: React.FC<{
  currentStatus: TranscriptionStatus;
  className?: string;
}> = ({ currentStatus, className }) => {
  const statuses = [
    TranscriptionStatus.PENDING,
    TranscriptionStatus.PROCESSING,
    TranscriptionStatus.COMPLETED,
  ];

  const getStatusIndex = (status: TranscriptionStatus): number => {
    return statuses.indexOf(status);
  };

  const currentIndex = getStatusIndex(currentStatus);

  return (
    <div className={clsx('space-y-4', className)}>
      {statuses.map((status, index) => {
        const config = statusConfig[status];
        const Icon = config.icon;
        const isActive = index <= currentIndex;
        const isCurrent = index === currentIndex;

        return (
          <div key={status} className="flex items-center gap-4">
            <div className={clsx(
              'flex items-center justify-center w-8 h-8 rounded-full border-2',
              {
                'bg-blue-600 border-blue-600 text-white': isActive,
                'bg-white dark:bg-gray-800 border-gray-300 dark:border-gray-600 text-gray-400': !isActive,
              }
            )}>
              <Icon className={clsx(
                'h-4 w-4',
                isCurrent && config.animated && 'animate-spin'
              )} />
            </div>
            
            <div className="flex-1">
              <p className={clsx(
                'text-sm font-medium',
                isActive 
                  ? 'text-gray-900 dark:text-white' 
                  : 'text-gray-500 dark:text-gray-400'
              )}>
                {config.label}
              </p>
              <p className={clsx(
                'text-xs',
                isActive 
                  ? 'text-gray-600 dark:text-gray-300' 
                  : 'text-gray-400 dark:text-gray-500'
              )}>
                {config.description}
              </p>
            </div>
          </div>
        );
      })}
    </div>
  );
};
