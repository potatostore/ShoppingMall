FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY ShoppingWeb-api/ .
RUN chmod +x ./gradlew
CMD ["./gradlew", "bootRun"]