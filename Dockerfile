# Multi-stage build for Spring Boot application
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copy parent POM and module POMs first for better caching
COPY pom.xml .
COPY domain/pom.xml domain/
COPY persistence/pom.xml persistence/
COPY service/pom.xml service/
COPY api/pom.xml api/
COPY app/pom.xml app/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY domain/src domain/src
COPY persistence/src persistence/src
COPY service/src service/src
COPY api/src api/src
COPY app/src app/src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the JAR from builder stage
COPY --from=builder /app/app/target/e-commerce-app-customer-service.jar app.jar

# Create directory for QR codes
RUN mkdir -p /app/qrcodes && chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8099

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8099/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "app.jar"]
