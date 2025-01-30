FROM openjdk:23-jdk-slim
WORKDIR /app
COPY build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar", "--server.port=${PORT}", "--spring.datasource.url=${DATABASE_URL}"]

