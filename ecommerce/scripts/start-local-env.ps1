# E-Commerce Local Development Environment Startup Script (PowerShell)

Write-Host "🐙 Starting E-Commerce local development environment..." -ForegroundColor Cyan

# Start Docker Compose
Write-Host "📦 Starting Docker containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.local.yml up -d

# Wait for containers to be ready
Write-Host "⏳ Waiting for containers to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Health check
Write-Host "🔍 Checking service status..." -ForegroundColor Yellow

# PostgreSQL connection check
$pgResult = docker exec ecommerce-postgres pg_isready -U ecommerce_user -d ecommerce_local 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ PostgreSQL is ready." -ForegroundColor Green
} else {
    Write-Host "❌ PostgreSQL connection failed." -ForegroundColor Red
    exit 1
}

# Redis connection check
$redisResult = docker exec ecommerce-redis redis-cli ping 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Redis is ready." -ForegroundColor Green
} else {
    Write-Host "❌ Redis connection failed." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 Local development environment started successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "📊 Management Tools:" -ForegroundColor Cyan
Write-Host "  - pgAdmin: http://localhost:5050 (admin@ecommerce.local / admin123)"
Write-Host "  - Redis Commander: http://localhost:8081"
Write-Host ""
Write-Host "🔗 Database Connection Info:" -ForegroundColor Cyan
Write-Host "  - Host: localhost"
Write-Host "  - Port: 5432"
Write-Host "  - Database: ecommerce_local"
Write-Host "  - Username: ecommerce_user"
Write-Host "  - Password: ecommerce_password"
Write-Host ""
Write-Host "🚀 To start the application:" -ForegroundColor Cyan
Write-Host "  .\gradlew.bat bootRun --args='--spring.profiles.active=local'"
Write-Host ""

Read-Host "Press Enter to continue"