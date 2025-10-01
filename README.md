# Backend

Spring Boot 기반 백엔드 API 서버의 로컬 개발 환경 설정 가이드입니다.

## 개발 환경 설정

### 🛠️ 필수 요구사항
- Java 17 이상
- Docker & Docker Compose
- Git

### 🔧 권장 도구
- IntelliJ IDEA 또는 VS Code
- Postman (API 테스트용)

## 로컬 실행 방법

### 1️⃣ Git 최신화
```sh
git fetch origin
git checkout main
git pull origin main
```

### 2️⃣ Gradle 빌드 (테스트 포함)
```sh
# macOS / Linux
./gradlew clean build

# Windows
gradlew.bat clean build
```

### 3️⃣ Docker 컨테이너 실행 (DB, 서버 포함)
```sh
docker compose up --build -d
```

## 로그 확인 방법

### Spring 서버 로그
```sh
docker compose logs -f spring-app
```

### MariaDB 로그
```sh
docker compose logs -f mariadb
```

### 전체 로그 실시간 모니터링
```sh
docker compose logs -f
```

## 접속 정보

- **Spring 애플리케이션**: http://localhost:8080
- **API 문서 (Swagger UI)**: http://localhost:8080/swagger-ui/index.html#/
- **MariaDB**: localhost:3306
  - 데이터베이스: `hi_meow`
  - 사용자: `admin`
  - 비밀번호: `1234`

## 개발 도구 설정

### IntelliJ IDEA 설정
1. **Project SDK**: Java 17
2. **Gradle JVM**: Java 17
3. **코드 스타일**: [CODING_CONVENTIONS.md](CODING_CONVENTIONS.md) 참조

### VS Code 설정 (선택사항)
필수 확장 프로그램:
- Extension Pack for Java
- Spring Boot Extension Pack
- Docker

## 문제 해결

### 자주 발생하는 문제들

#### 🚫 포트 충돌 (8080 포트 사용 중)
```sh
# 포트 사용 프로세스 확인 및 종료
lsof -ti:8080 | xargs kill -9

# 또는 docker compose 완전 정리 후 재시작
docker compose down
docker compose up --build -d
```

#### 🚫 Docker 빌드 실패
```sh
# Docker 캐시 정리
docker system prune -a

# 컨테이너 완전 재생성
docker compose down -v
docker compose up --build -d
```

#### 🚫 Gradle 빌드 실패
```sh
# Gradle 캐시 정리
./gradlew clean

# 의존성 새로 다운로드
./gradlew build --refresh-dependencies
```

#### 🚫 데이터베이스 연결 오류
```sh
# MariaDB 컨테이너 재시작
docker compose restart mariadb

# 데이터베이스 초기화 (주의: 데이터 삭제됨)
docker compose down -v
docker compose up -d
```

### 디버깅 명령어
```sh
# 컨테이너 상태 확인
docker compose ps

# 특정 컨테이너 로그 확인
docker compose logs spring-app --tail=50

# 컨테이너 내부 접속
docker compose exec spring-app bash
docker compose exec mariadb mysql -u admin -p hi_meow
```

## 관련 문서

### 개발 가이드
- 📝 [코딩 컨벤션](docs/CODING_CONVENTIONS.md)
- 🔀 [커밋 규칙](docs/COMMIT_RULES.md)

### 프로젝트 문서  
- 📖 [이슈 추적 히스토리](docs/ISSUES.md)

### API 문서
- 🔍 [API 명세서](http://localhost:8080/swagger-ui/index.html#/) - 로컬 개발 환경
