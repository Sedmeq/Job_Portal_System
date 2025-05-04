# Rəsmi OpenJDK 17 istifadə edirik (yüngül versiya - slim)
FROM openjdk:17-jdk-slim

# Konteynerin içində iş qovluğunu təyin edirik
WORKDIR /app

# Build edilən JAR faylını konteynerə kopyalayırıq
COPY build/libs/Job_Portal-0.0.1-SNAPSHOT.jar /app/Job_Portal.jar

# Konteynerdə işləyən portu açırıq (Spring Boot default 8080)
EXPOSE 8080

# Spring Boot tətbiqini başlatmaq üçün komanda
ENTRYPOINT ["java", "-jar", "/app/Job_Portal.jar"]