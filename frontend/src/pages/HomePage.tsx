// Home Page Component

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FileUpload } from '../components/upload/FileUpload';
import { Button } from '../components/common/Button';
import { useTranscription } from '../hooks/useTranscription';
import { Upload, Mic, Zap, Shield } from 'lucide-react';

export const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  
  const {
    uploadAudio,
    isUploading,
    error,
    clearError,
    currentJobId,
  } = useTranscription();

  const handleFileSelect = (file: File) => {
    setSelectedFile(file);
    clearError();
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    try {
      await uploadAudio(selectedFile);
    } catch (error) {
      console.error('Upload failed:', error);
    }
  };

  // Navigate to status page when job ID is received
  React.useEffect(() => {
    if (currentJobId) {
      navigate(`/status/${currentJobId}`);
    }
  }, [currentJobId, navigate]);

  const handleStartOver = () => {
    setSelectedFile(null);
    clearError();
  };

  return (
    <div className="space-y-8">
      {/* Hero Section */}
      <div className="text-center space-y-6">
        <div className="flex justify-center">
          <div className="p-4 bg-blue-100 dark:bg-blue-900/20 rounded-full">
            <Mic className="h-12 w-12 text-blue-600 dark:text-blue-400" />
          </div>
        </div>
        
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white">
          Transform Audio to Text
        </h1>
        
        <p className="text-xl text-gray-600 dark:text-gray-300 max-w-2xl mx-auto">
          Upload your audio files and get accurate transcriptions powered by AI. 
          Support for multiple formats with high-quality results.
        </p>
      </div>

      {/* Features */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="text-center p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
          <div className="flex justify-center mb-4">
            <Zap className="h-8 w-8 text-yellow-500" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
            Fast Processing
          </h3>
          <p className="text-gray-600 dark:text-gray-300">
            Get your transcriptions in minutes with our optimized AI processing
          </p>
        </div>
        
        <div className="text-center p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
          <div className="flex justify-center mb-4">
            <Shield className="h-8 w-8 text-green-500" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
            Secure & Private
          </h3>
          <p className="text-gray-600 dark:text-gray-300">
            Your files are processed securely and deleted after transcription
          </p>
        </div>
        
        <div className="text-center p-6 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700">
          <div className="flex justify-center mb-4">
            <Upload className="h-8 w-8 text-blue-500" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
            Multiple Formats
          </h3>
          <p className="text-gray-600 dark:text-gray-300">
            Support for MP3, WAV, M4A, FLAC, OGG, and WMA files up to 25MB
          </p>
        </div>
      </div>

      {/* Upload Section */}
      <div className="max-w-2xl mx-auto">
        <div className="card p-8">
          <div className="space-y-6">
            <div className="text-center">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
                Upload Your Audio
              </h2>
              <p className="text-gray-600 dark:text-gray-300">
                Drag and drop your audio file or click to browse
              </p>
            </div>

            <FileUpload
              onFileSelect={handleFileSelect}
              disabled={isUploading}
            />

            {selectedFile && (
              <div className="space-y-4">
                <div className="flex justify-center">
                  <Button
                    onClick={handleUpload}
                    loading={isUploading}
                    disabled={isUploading}
                    size="lg"
                    className="w-full md:w-auto"
                  >
                    {isUploading ? 'Uploading...' : 'Start Transcription'}
                  </Button>
                </div>
                
                <div className="text-center">
                  <Button
                    variant="ghost"
                    onClick={handleStartOver}
                    disabled={isUploading}
                  >
                    Choose Different File
                  </Button>
                </div>
              </div>
            )}

            {error && (
              <div className="p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                <p className="text-sm text-red-700 dark:text-red-300 text-center">
                  {error}
                </p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Instructions */}
      <div className="max-w-4xl mx-auto">
        <div className="card p-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            How it works
          </h3>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center">
              <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/20 rounded-full flex items-center justify-center mx-auto mb-3">
                <span className="text-blue-600 dark:text-blue-400 font-bold">1</span>
              </div>
              <h4 className="font-medium text-gray-900 dark:text-white mb-2">
                Upload Audio
              </h4>
              <p className="text-sm text-gray-600 dark:text-gray-300">
                Select your audio file using drag & drop or file browser
              </p>
            </div>
            
            <div className="text-center">
              <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/20 rounded-full flex items-center justify-center mx-auto mb-3">
                <span className="text-blue-600 dark:text-blue-400 font-bold">2</span>
              </div>
              <h4 className="font-medium text-gray-900 dark:text-white mb-2">
                AI Processing
              </h4>
              <p className="text-sm text-gray-600 dark:text-gray-300">
                Our AI analyzes and transcribes your audio with high accuracy
              </p>
            </div>
            
            <div className="text-center">
              <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/20 rounded-full flex items-center justify-center mx-auto mb-3">
                <span className="text-blue-600 dark:text-blue-400 font-bold">3</span>
              </div>
              <h4 className="font-medium text-gray-900 dark:text-white mb-2">
                Get Results
              </h4>
              <p className="text-sm text-gray-600 dark:text-gray-300">
                Download your transcription in text or JSON format
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
