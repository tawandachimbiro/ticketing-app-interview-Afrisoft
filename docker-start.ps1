# Docker Start Script for Ticketing Application
# This script helps you quickly start the application with Docker

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Ticketing Application - Docker" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if Docker is running
Write-Host "Checking Docker status..." -ForegroundColor Yellow
$dockerRunning = docker info 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Docker is not running!" -ForegroundColor Red
    Write-Host "Please start Docker Desktop and try again." -ForegroundColor Red
    exit 1
}
Write-Host "Docker is running" -ForegroundColor Green
Write-Host ""

# Check if .env file exists
if (-not (Test-Path ".env")) {
    Write-Host "Creating .env file from .env.example..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "Please update the .env file with your actual values" -ForegroundColor Yellow
    Write-Host ""
}

# Ask user what to do
Write-Host "What would you like to do?" -ForegroundColor Cyan
Write-Host "1. Build and start all services" -ForegroundColor White
Write-Host "2. Start existing services" -ForegroundColor White
Write-Host "3. Stop all services" -ForegroundColor White
Write-Host "4. View logs" -ForegroundColor White
Write-Host "5. Rebuild application only" -ForegroundColor White
Write-Host "6. Clean up (stop and remove containers)" -ForegroundColor White
Write-Host ""

$choice = Read-Host "Enter your choice (1-6)"

switch ($choice) {
    "1" {
        Write-Host "Building and starting all services..." -ForegroundColor Yellow
        docker-compose up -d --build
        Write-Host ""
        Write-Host "Services started successfully!" -ForegroundColor Green
        Write-Host "Application: http://localhost:8099" -ForegroundColor Cyan
        Write-Host "API Docs: http://localhost:8099/swagger-ui.html" -ForegroundColor Cyan
        Write-Host "phpMyAdmin: http://localhost:8080" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "To view logs, run: docker-compose logs -f" -ForegroundColor Yellow
    }
    "2" {
        Write-Host "Starting services..." -ForegroundColor Yellow
        docker-compose up -d
        Write-Host "Services started!" -ForegroundColor Green
    }
    "3" {
        Write-Host "Stopping services..." -ForegroundColor Yellow
        docker-compose down
        Write-Host "Services stopped!" -ForegroundColor Green
    }
    "4" {
        Write-Host "Showing logs (Press Ctrl+C to exit)..." -ForegroundColor Yellow
        docker-compose logs -f
    }
    "5" {
        Write-Host "Rebuilding application..." -ForegroundColor Yellow
        docker-compose up -d --build ticketing-app
        Write-Host "Application rebuilt and restarted!" -ForegroundColor Green
    }
    "6" {
        $confirm = Read-Host "This will remove all containers. Continue? (y/n)"
        if ($confirm -eq "y") {
            Write-Host "Cleaning up..." -ForegroundColor Yellow
            docker-compose down -v
            Write-Host "Cleanup complete!" -ForegroundColor Green
        }
    }
    default {
        Write-Host "Invalid choice!" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
