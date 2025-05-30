# Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set memory-optimized JVM options for Railway
ENV JAVA_OPTS="-Xmx400m -Xms200m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=32m -XX:+UseG1GC -XX:G1HeapRegionSize=8m -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Create app directory and user for security
RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup
WORKDIR /app

# Copy WAR file from build stage
COPY --from=builder /app/target/*.war app.war

# Change ownership to app user
RUN chown -R appuser:appgroup /app
USER appuser

# Expose port
EXPOSE 8080

# Health check with reduced frequency to save resources
HEALTHCHECK --interval=60s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Run with optimized settings
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.war"] 