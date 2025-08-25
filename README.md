# Backend

Spring Boot 애플리케이션의 로컬 개발 환경 실행 및 Docker 사용 가이드입니다.

<br>

### 🚀 로컬 실행 방법

##### 1️⃣ Git 최신화
```sh
git fetch origin
git checkout main
git pull origin main
```

##### 2️⃣ Gradle 빌드 (테스트 포함)
```sh
# macOS / Linux
./gradlew clean build

# window
gradlew.bat clean build
```

##### 3️⃣ Docker 컨테이너 실행 (DB, 서버 포함)
```sh
docker compose up --build -d
```

<br>

### 🔍 로그 확인 방법

✅ Spring 서버 로그
```sh
docker compose logs -f spring-app
```

✅ MariaDB 로그
```sh
docker compose logs -f mariadb
```

<br>

### 🌐 기본 로컬 환경 접속 정보

- Spring 애플리케이션 → http://localhost:8080
- Swagger UI (API 문서) → http://localhost:8080/swagger-ui/index.html
- MariaDB → localhost:3306
  - 데이터베이스: hi_meow
  - 사용자: admin
  - 비밀번호: 1234

