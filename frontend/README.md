# Whisperrr Frontend

React TypeScript frontend for the Whisperrr audio transcription platform.

## Features

- 🎤 **Audio Upload**: Drag-and-drop file upload with validation
- 📊 **Real-time Status**: Live progress tracking with polling
- 📝 **Results Display**: Formatted transcription with segments
- 📱 **Responsive Design**: Mobile-friendly interface
- 🎨 **Modern UI**: Clean design with Tailwind CSS
- ⚡ **Fast Performance**: Optimized with React Query

## Tech Stack

- **React 18** with TypeScript
- **React Router** for navigation
- **React Query** for data fetching and caching
- **Tailwind CSS** for styling
- **Lucide React** for icons
- **React Hook Form** for form handling
- **React Dropzone** for file uploads

## Getting Started

### Prerequisites

- Node.js 16+ 
- npm or yarn

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm start
```

The app will be available at `http://localhost:3000`.

### Environment Variables

Create a `.env` file in the root directory:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_POLLING_INTERVAL=2000
```

## Project Structure

```
src/
├── components/          # Reusable UI components
│   ├── common/         # Common components (Button, Loading, etc.)
│   ├── upload/         # File upload components
│   ├── status/         # Status display components
│   └── results/        # Results display components
├── hooks/              # Custom React hooks
├── pages/              # Page components
├── services/           # API service layer
├── types/              # TypeScript type definitions
├── utils/              # Utility functions
└── styles/             # Global styles
```

## Key Components

### FileUpload
- Drag-and-drop file upload
- File validation (type, size)
- Visual feedback for different states

### StatusDisplay
- Real-time status updates
- Progress tracking
- Timeline visualization

### ResultsView
- Formatted transcription display
- Segment-by-segment view
- Export functionality (TXT, JSON)

## API Integration

The frontend communicates with the Spring Boot backend through:

- `POST /api/audio/upload` - Upload audio file
- `GET /api/audio/status/{jobId}` - Get transcription status
- `GET /api/audio/result/{jobId}` - Get transcription result

## Development

### Available Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm run lint` - Run ESLint
- `npm run type-check` - Run TypeScript compiler

### Code Style

- TypeScript strict mode
- ESLint with React rules
- Prettier for formatting
- Tailwind CSS for styling

## Deployment

The frontend is containerized with Docker and can be deployed alongside the backend services.

```bash
# Build Docker image
docker build -t whisperrr-frontend .

# Run container
docker run -p 3000:80 whisperrr-frontend
```

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Follow TypeScript best practices
2. Use functional components with hooks
3. Implement proper error handling
4. Add loading states for async operations
5. Ensure mobile responsiveness
6. Write clean, maintainable code