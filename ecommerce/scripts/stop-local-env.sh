#!/bin/bash

# 로컬 개발 환경 종료 스크립트

echo "🐙 E-Commerce 로컬 개발 환경을 종료합니다..."

# Docker Compose 종료
echo "📦 Docker 컨테이너들을 종료하는 중..."
docker-compose -f docker-compose.local.yml down

echo "🧹 사용하지 않는 Docker 리소스 정리 중..."
docker system prune -f > /dev/null 2>&1

echo "✅ 로컬 개발 환경이 종료되었습니다."
echo ""
echo "💡 데이터를 완전히 삭제하려면 다음 명령어를 실행하세요:"
echo "  docker-compose -f docker-compose.local.yml down -v"
echo ""