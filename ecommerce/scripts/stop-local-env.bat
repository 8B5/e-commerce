@echo off
chcp 65001 > nul
REM E-Commerce Local Development Environment Shutdown Script (Windows)

echo Stopping E-Commerce local development environment...

REM Stop Docker Compose
echo Stopping Docker containers...
docker-compose -f docker-compose.local.yml down

echo Cleaning up unused Docker resources...
docker system prune -f > nul 2>&1

echo Local development environment stopped successfully.
echo.
echo To completely remove data, run:
echo   docker-compose -f docker-compose.local.yml down -v
echo.

pause