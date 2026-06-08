# =====================================================================
# Build Stage
# =====================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# =====================================================================
# Run Stage
# =====================================================================
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/codecollab-backend-0.0.1-SNAPSHOT.jar app.jar

# Spring Boot reads the PORT env var injected by Render
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
