FROM gradle:jdk17 as builder

WORKDIR /app
COPY . .
RUN ./gradlew clean build

FROM openjdk:17-slim

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 