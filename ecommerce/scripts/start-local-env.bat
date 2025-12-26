@echo off
chcp 65001 > nul
REM E-Commerce Local Development Environment Startup Script (Windows)

echo Starting E-Commerce local development environment...

REM Start Docker Compose
echo Starting Docker containers...
docker-compose -f docker-compose.local.yml up -d

REM Wait for containers to be ready
echo Waiting for containers to be ready...
timeout /t 10 /nobreak > nul

REM Health check
echo Checking service status...

REM PostgreSQL connection check
docker exec ecommerce-postgres pg_isready -U ecommerce_user -d ecommerce_local > nul 2>&1
if %errorlevel% == 0 (
    echo [OK] PostgreSQL is ready.
) else (
    echo [ERROR] PostgreSQL connection failed.
    exit /b 1
)

REM Redis connection check
docker exec ecommerce-redis redis-cli ping > nul 2>&1
if %errorlevel% == 0 (
    echo [OK] Redis is ready.
) else (
    echo [ERROR] Redis connection failed.
    exit /b 1
)

echo.
echo Local development environment started successfully!
echo.
echo Management Tools:
echo   - pgAdmin: http://localhost:5050 (admin@ecommerce.local / admin123)
echo   - Redis Commander: http://localhost:8081
echo.
echo Database Connection Info:
echo   - Host: localhost
echo   - Port: 5432
echo   - Database: ecommerce_local
echo   - Username: ecommerce_user
echo   - Password: ecommerce_password
echo.
echo To start the application:
echo   gradlew.bat bootRun --args="--spring.profiles.active=local"
echo.

pause