#!/bin/bash

# Simple Whisperrr Setup Script
# This script sets up the development environment step by step

set -e

echo "🚀 Whisperrr Development Setup"
echo "=============================="

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if we're in the right directory
if [ ! -f "README.md" ] || [ ! -d "backend" ] || [ ! -d "python-service" ] || [ ! -d "frontend" ]; then
    echo "❌ Please run this script from the Whisperrr root directory"
    exit 1
fi

echo ""
print_status "Setting up Python service..."

cd python-service

# Create virtual environment if it doesn't exist
if [ ! -d "venv" ]; then
    print_status "Creating Python virtual environment..."
    python3 -m venv venv
    print_success "Virtual environment created"
fi

# Install dependencies
print_status "Installing Python dependencies..."
./venv/bin/python -m pip install --upgrade pip

# Try to install dependencies, with fallback for whisper
print_status "Installing core dependencies..."
./venv/bin/python -m pip install fastapi uvicorn python-multipart pydantic python-dotenv httpx librosa soundfile

print_status "Installing PyTorch..."
./venv/bin/python -m pip install torch

print_status "Installing OpenAI Whisper (this may take a while)..."
if ./venv/bin/python -m pip install openai-whisper; then
    print_success "Whisper installed successfully"
else
    print_warning "Whisper installation failed. You may need to:"
    echo "  1. Use Python 3.11 or 3.12 instead of 3.13"
    echo "  2. Install from conda: conda install -c conda-forge openai-whisper"
    echo "  3. Use Docker instead: docker-compose up --build"
fi

# Copy environment file
if [ ! -f ".env" ]; then
    cp env.example .env
    print_success "Created .env file"
fi

cd ..
print_success "Python service setup complete"

echo ""
print_status "Setting up Spring Boot backend..."

cd backend

# Build the project
print_status "Building Spring Boot project..."
./mvnw clean compile

cd ..
print_success "Backend setup complete"

echo ""
print_status "Setting up React frontend..."

cd frontend

# Install dependencies
print_status "Installing Node.js dependencies..."
npm install

# Copy environment file
if [ ! -f ".env" ]; then
    cp env.example .env
    print_success "Created .env file"
fi

cd ..
print_success "Frontend setup complete"

echo ""
print_success "🎉 Setup completed successfully!"
echo ""
echo "To start the services:"
echo ""
echo "1. Python service (Terminal 1):"
echo "   cd python-service"
echo "   source venv/bin/activate"
echo "   python -m app.main"
echo ""
echo "2. Spring Boot backend (Terminal 2):"
echo "   cd backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "3. React frontend (Terminal 3):"
echo "   cd frontend"
echo "   npm start"
echo ""
echo "Then open http://localhost:3000 in your browser"
echo ""
print_warning "Note: Make sure PostgreSQL is running and the database is set up:"
echo "   createdb transcription_db"
echo "   psql -c \"CREATE USER transcription_user WITH PASSWORD 'transcription_pass';\""
echo "   psql -c \"GRANT ALL PRIVILEGES ON DATABASE transcription_db TO transcription_user;\""
