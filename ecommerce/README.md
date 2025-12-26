# E-Commerce MSA Platform

## 📋 프로젝트 개요

Java 21과 Spring Boot 3.x 기반의 확장 가능하고 안전한 MSA(Microservices Architecture) 쇼핑몰 플랫폼입니다.

## 🛠 기술 스택

### Core Technologies
- **JDK**: 21
- **Spring Boot**: 3.5.x
- **Spring Cloud Stack**: MSA 구성
- **JPA**: 데이터 접근 계층
- **PostgreSQL**: 메인 데이터베이스
- **Lombok**: 코드 간소화

### Architecture & Design Principles
- **MSA (Microservices Architecture)**: 서비스별 독립적 배포 및 확장
- **SOLID 원칙**: 특히 의존성 역전(DIP)을 통한 인프라와 도메인 분리
- **Clean Architecture**: 계층별 명확한 책임 분리

### Security & Data Protection
- **ScpDbCryptoUtil**: 데이터베이스 암호화 (AES-256-GCM)
- **MaskingUtils**: 개인정보 마스킹 처리
- **JWT**: 토큰 기반 인증

### Testing
- **JUnit 5**: 단위 테스트 프레임워크
- **Mockito**: 모킹 라이브러리

## 🏗 프로젝트 구조

```
src/main/java/com/ecommerce/
├── common/
│   # 공통 모듈
│   ├── code/
│   │   # 응답 코드 정의
│   │   ├── CommonResultCode.java    # 범용 결과 코드
│   ├── crypto/
│   │   # 암호화 관련
│   │   ├── DefaultScpDbCryptoUtil.java    # 기본 암호화 구현체
│   │   ├── ScpDbCryptoUtil.java    # 암호화 인터페이스
│   ├── dto/
│   │   # DTO
│   ├── enums/
│   ├── response/
│   │   # 응답 DTO
│   │   ├── ApiResponse.java    # 공통 응답 구조
│   ├── util/
│   │   # 유틸리티 클래스
│   │   ├── MaskingUtils.java    # 개인정보 마스킹
│   ├── utils/
├── ecommerce/
│   ├── EcommerceApplication.java    # 메인 클래스
│   ├── ServletInitializer.java    # 서블릿 초기화
├── user/
│   # 회원 서비스
│   ├── application/
│   │   # 애플리케이션 계층
│   │   ├── dto/
│   │   │   # DTO
│   │   │   ├── request/
│   │   │   │   # 요청 DTO
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── SignupRequest.java
│   │   │   ├── response/
│   │   │   │   # 응답 DTO
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── UserProfileResponse.java
│   │   ├── service/
│   │   │   # 서비스
│   │   │   ├── AuthService.java    # 인증 서비스
│   ├── code/
│   │   # 응답 코드 정의
│   │   ├── LoginResultCode.java    # 로그인 결과 코드
│   ├── domain/
│   │   # 도메인 계층
│   │   ├── entity/
│   │   │   # 엔티티
│   │   │   ├── Cart.java    # 장바구니
│   │   │   ├── CartItem.java    # 장바구니 아이템
│   │   │   ├── User.java    # 사용자 엔티티
│   │   │   ├── UserRole.java    # 사용자 권한
│   │   │   ├── Wishlist.java    # 찜 목록
│   │   ├── repository/
│   │   │   # 리포지토리 인터페이스
│   │   │   ├── CartRepository.java
│   │   │   ├── UserRepository.java
│   │   │   ├── WishlistRepository.java
│   ├── infrastructure/
│   │   # 인프라 계층
│   │   ├── security/
│   │   │   # 보안 관련
│   │   │   ├── InMemoryRefreshTokenStore.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── RefreshTokenStore.java
```


## 🔧 환경 설정

### 필수 환경 변수

```properties
app.crypto.secret-key=                # AES-256 암호화 키 (Base64 인코딩)
app.jwt.secret=                # JWT 서명 키
app.jwt.access-token-expiration=                # 설정 값
app.jwt.refresh-token-expiration=                # 설정 값
```

## 🚀 시작하기

### 0. 터미널 설정 (Windows 사용자)
Windows에서 한글이 깨지는 경우 다음 스크립트를 실행하세요:
```bash
# 터미널 인코딩 설정
scripts\setup-terminal.bat

# 인코딩 테스트
scripts\test-encoding.bat
```

**권장사항:**
- Windows Terminal 사용 (Command Prompt 대신)
- PowerShell 스크립트 사용 (`.ps1` 파일)
- 한글 지원 폰트 설정 (D2Coding, Consolas 등)

### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd ecommerce
```

### 2. 로컬 개발 환경 시작
```bash
# Windows (Command Prompt)
scripts\start-local-env.bat

# Windows (PowerShell) - 권장
scripts\start-local-env.ps1

# Linux/Mac
scripts/start-local-env.sh
```

### 3. 환경 설정
`src/main/resources/application.properties` 파일에 필요한 환경 변수를 설정합니다.

### 4. 빌드 및 실행
```bash
# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 애플리케이션 실행 (로컬 환경)
./gradlew bootRun --args='--spring.profiles.active=local'

# Windows
gradlew.bat bootRun --args="--spring.profiles.active=local"
```

### 5. 개발 환경 종료
```bash
# Windows (Command Prompt)
scripts\stop-local-env.bat

# Windows (PowerShell)
scripts\stop-local-env.ps1

# Linux/Mac
scripts/stop-local-env.sh
```

## 📊 주요 기능

### 공통 모듈 (Common Module)

#### 1. 공통 응답 포맷 (ApiResponse)
- 모든 API에서 일관된 응답 구조 제공
- 성공/실패 상태, 메시지, 데이터, 타임스탬프 포함

```java
// 성공 응답
ApiResponse<UserData> response = ApiResponse.success(userData);

// 실패 응답
ApiResponse<Void> response = ApiResponse.failure(CommonResultCode.INVALID_PARAMETER);
```

#### 2. 개인정보 마스킹 (MaskingUtils)
- 이름, 이메일, 전화번호 마스킹 처리
- GDPR 및 개인정보보호법 준수

```java
String maskedName = MaskingUtils.maskName("홍길동");        // 홍*동
String maskedEmail = MaskingUtils.maskEmail("test@example.com"); // tes*@example.com
String maskedPhone = MaskingUtils.maskPhoneNumber("010-1234-5678"); // 010-****-5678
```

#### 3. 데이터베이스 암호화 (ScpDbCryptoUtil)
- AES-256-GCM 양방향 암호화
- SHA-256 기반 비밀번호 해싱 (Salt 포함)
- 인터페이스 기반 설계로 구현체 교체 가능

```java
// 양방향 암호화
String encrypted = cryptoUtil.encrypt("민감한 데이터");
String decrypted = cryptoUtil.decrypt(encrypted);

// 비밀번호 해싱
String hashedPassword = cryptoUtil.hashPassword("myPassword");
boolean isValid = cryptoUtil.verifyPassword("myPassword", hashedPassword);
```

### 회원 서비스 (User Service)

#### 1. 사용자 관리
- 회원가입, 로그인, 프로필 관리
- 역할 기반 권한 관리 (구매자/판매자/관리자)
- JWT 기반 인증 시스템

#### 2. 장바구니 시스템
- 상품 추가/삭제/수량 변경
- 사용자별 장바구니 관리

#### 3. 찜 목록
- 관심 상품 저장 및 관리

## 🧪 테스트

### 테스트 실행
```bash
# 전체 테스트
./gradlew test

# 특정 패키지 테스트
./gradlew test --tests "com.ecommerce.common.*"
./gradlew test --tests "com.ecommerce.user.*"
```

### 테스트 커버리지
- 단위 테스트: 비즈니스 로직 중심
- 통합 테스트: API 엔드포인트 검증
- 모킹: 외부 의존성 격리

## 🔒 보안 고려사항

### 1. 데이터 보호
- 개인정보 자동 마스킹
- 민감 데이터 암호화 저장
- 비밀번호 단방향 해싱

### 2. 인증/인가
- JWT 토큰 기반 인증
- 리프레시 토큰을 통한 보안 강화
- 역할 기반 접근 제어

### 3. API 보안
- 입력 값 검증
- SQL 인젝션 방지
- XSS 공격 방지

## 🚧 향후 계획

### Phase 1: 기본 서비스 구현
- [x] 공통 모듈 구현
- [x] 회원 서비스 기본 기능
- [ ] 구매자 상품 서비스
- [ ] 판매자 상품 서비스
- [ ] 주문 서비스

### Phase 2: 고도화
- [ ] 결제 서비스 연동
- [ ] 재고 관리 시스템
- [ ] 배송 관리 시스템
- [ ] 알림 서비스

### Phase 3: 운영 최적화
- [ ] 모니터링 및 로깅
- [ ] 성능 최적화
- [ ] 장애 대응 시스템
- [ ] CI/CD 파이프라인

## 📝 개발 가이드라인

### 코딩 컨벤션
- Java 21 최신 문법 활용
- SOLID 원칙 준수
- Clean Code 작성
- 적절한 주석 및 문서화

### 커밋 메시지
```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 코드 추가/수정
chore: 빌드 설정 등 기타 변경
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

---

**Note**: 프로젝트 구조가 변경되거나 새로운 환경 변수가 생기면 이 README.md 파일을 자동으로 업데이트합니다.

*Last updated: 2025-12-23 09:46:20*