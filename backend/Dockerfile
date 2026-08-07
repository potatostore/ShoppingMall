# 1. Build Stage: JAR 파일 빌드
FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app

# Gradle 의존성 캐싱
COPY shopping-mall-api/gradlew .
COPY shopping-mall-api/gradle gradle
COPY shopping-mall-api/build.gradle .
COPY shopping-mall-api/settings.gradle .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 JAR 빌드
COPY shopping-mall-api/src src
RUN ./gradlew bootJar -x test --no-daemon

# 2. Runtime Stage: 경량화된 실행 환경
FROM amazoncorretto:21-alpine
WORKDIR /app

# 빌드 단계에서 생성된 jar 파일만 복사
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# 컨테이너 포트 노출 (기본 8080)
EXPOSE 8080

# JAR 파일 직접 실행
ENTRYPOINT ["java", "-jar", "app.jar"]