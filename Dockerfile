FROM mcr.microsoft.com/playwright/java:v1.57.0-noble

# Установка зависимостей проекта
WORKDIR /app
COPY pom.xml ./
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn clean install