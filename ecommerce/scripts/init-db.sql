-- E-Commerce 로컬 개발 환경 초기 데이터베이스 설정

-- 확장 기능 활성화
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- 스키마 생성 (필요시)
-- CREATE SCHEMA IF NOT EXISTS ecommerce;

-- 기본 테이블 생성은 JPA가 담당하므로 여기서는 초기 데이터만 설정

-- 개발용 초기 데이터 (필요시 추가)
-- INSERT INTO users (id, email, name, role, created_at) 
-- VALUES (uuid_generate_v4(), 'admin@ecommerce.local', 'Admin User', 'ADMIN', NOW())
-- ON CONFLICT DO NOTHING;

-- 인덱스 생성 (성능 최적화)
-- CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
-- CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);

-- 개발 환경 설정 완료 로그
SELECT 'E-Commerce 로컬 개발 데이터베이스 초기화 완료' AS message;