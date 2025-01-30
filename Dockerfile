FROM openjdk:23-jdk-slim
WORKDIR /app
COPY gradle/wrapper/gradle-wrapper.jar app.jar
CMD ["java", "-jar", "app.jar"]
