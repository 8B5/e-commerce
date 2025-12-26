#!/bin/bash

# 로컬 개발 환경 시작 스크립트

echo "🐙 E-Commerce 로컬 개발 환경을 시작합니다..."

# Docker Compose 실행
echo "📦 Docker 컨테이너들을 시작하는 중..."
docker-compose -f docker-compose.local.yml up -d

# 컨테이너 상태 확인
echo "⏳ 컨테이너들이 준비될 때까지 대기 중..."
sleep 10

# 헬스체크
echo "🔍 서비스 상태 확인 중..."

# PostgreSQL 연결 확인
if docker exec ecommerce-postgres pg_isready -U ecommerce_user -d ecommerce_local > /dev/null 2>&1; then
    echo "✅ PostgreSQL이 준비되었습니다."
else
    echo "❌ PostgreSQL 연결에 실패했습니다."
    exit 1
fi

# Redis 연결 확인
if docker exec ecommerce-redis redis-cli ping > /dev/null 2>&1; then
    echo "✅ Redis가 준비되었습니다."
else
    echo "❌ Redis 연결에 실패했습니다."
    exit 1
fi

echo ""
echo "🎉 로컬 개발 환경이 성공적으로 시작되었습니다!"
echo ""
echo "📊 관리 도구 접속 정보:"
echo "  - pgAdmin: http://localhost:5050 (admin@ecommerce.local / admin123)"
echo "  - Redis Commander: http://localhost:8081"
echo ""
echo "🔗 데이터베이스 연결 정보:"
echo "  - Host: localhost"
echo "  - Port: 5432"
echo "  - Database: ecommerce_local"
echo "  - Username: ecommerce_user"
echo "  - Password: ecommerce_password"
echo ""
echo "🚀 애플리케이션을 시작하려면:"
echo "  ./gradlew bootRun --args='--spring.profiles.active=local'"
echo ""