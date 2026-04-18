# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy the Maven wrapper and pom.xml to cache dependencies
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Convert line endings of mvnw to LF (in case it was edited on Windows) and make it executable
RUN sed -i 's/\r$//' mvnw \
    && chmod +x ./mvnw \
    && ./mvnw dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the optimized runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user and group for better security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Define the build argument for the active profile
ARG PROFILE

# Set the environment variable used by Spring Boot to determine the active profile
ENV SPRING_PROFILES_ACTIVE=${PROFILE}

# Copy the built JAR from the builder stage
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]