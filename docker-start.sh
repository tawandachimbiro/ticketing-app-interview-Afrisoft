#!/bin/bash
# Docker Start Script for Ticketing Application (Linux/Mac)

echo "====================================="
echo "  Ticketing Application - Docker"
echo "====================================="
echo ""

# Check if Docker is running
echo "Checking Docker status..."
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker is not running!"
    echo "Please start Docker and try again."
    exit 1
fi
echo "Docker is running"
echo ""

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo "Creating .env file from .env.example..."
    cp .env.example .env
    echo "Please update the .env file with your actual values"
    echo ""
fi

# Ask user what to do
echo "What would you like to do?"
echo "1. Build and start all services"
echo "2. Start existing services"
echo "3. Stop all services"
echo "4. View logs"
echo "5. Rebuild application only"
echo "6. Clean up (stop and remove containers)"
echo ""

read -p "Enter your choice (1-6): " choice

case $choice in
    1)
        echo "Building and starting all services..."
        docker-compose up -d --build
        echo ""
        echo "Services started successfully!"
        echo "Application: http://localhost:8099"
        echo "API Docs: http://localhost:8099/swagger-ui.html"
        echo "phpMyAdmin: http://localhost:8080"
        echo ""
        echo "To view logs, run: docker-compose logs -f"
        ;;
    2)
        echo "Starting services..."
        docker-compose up -d
        echo "Services started!"
        ;;
    3)
        echo "Stopping services..."
        docker-compose down
        echo "Services stopped!"
        ;;
    4)
        echo "Showing logs (Press Ctrl+C to exit)..."
        docker-compose logs -f
        ;;
    5)
        echo "Rebuilding application..."
        docker-compose up -d --build ticketing-app
        echo "Application rebuilt and restarted!"
        ;;
    6)
        read -p "This will remove all containers. Continue? (y/n): " confirm
        if [ "$confirm" = "y" ]; then
            echo "Cleaning up..."
            docker-compose down -v
            echo "Cleanup complete!"
        fi
        ;;
    *)
        echo "Invalid choice!"
        ;;
esac

echo ""
echo "Done!"
