FROM gradle:jdk17 as builder

WORKDIR /app

# Gradle wrapper와 설정 파일들을 먼저 복사
COPY gradle/ gradle/
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# 의존성만 먼저 다운로드 (캐싱을 위해)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY src/ src/

# 빌드 실행
RUN ./gradlew clean build --no-daemon

FROM openjdk:17-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]