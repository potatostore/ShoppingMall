FROM amazoncorretto:21-alpine-jdk AS build
WORKDIR /app
COPY shopping-mall-api/gradlew .
COPY shopping-mall-api/gradle gradle
COPY shopping-mall-api/build.gradle .
COPY shopping-mall-api/settings.gradle .
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon
COPY shopping-mall-api/src src
RUN ./gradlew clean build -x test --no-daemon
CMD ["./gradlew", "bootRun"]