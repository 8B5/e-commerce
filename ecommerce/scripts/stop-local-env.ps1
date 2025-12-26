# E-Commerce Local Development Environment Shutdown Script (PowerShell)

Write-Host "🐙 Stopping E-Commerce local development environment..." -ForegroundColor Cyan

# Stop Docker Compose
Write-Host "📦 Stopping Docker containers..." -ForegroundColor Yellow
docker-compose -f docker-compose.local.yml down

Write-Host "🧹 Cleaning up unused Docker resources..." -ForegroundColor Yellow
docker system prune -f | Out-Null

Write-Host "✅ Local development environment stopped successfully." -ForegroundColor Green
Write-Host ""
Write-Host "💡 To completely remove data, run:" -ForegroundColor Cyan
Write-Host "  docker-compose -f docker-compose.local.yml down -v"
Write-Host ""

Read-Host "Press Enter to continue"