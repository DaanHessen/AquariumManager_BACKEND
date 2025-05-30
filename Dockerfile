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

# Runtime stage - Use Tomcat for WAR deployment
FROM tomcat:10.1-jre17

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Set memory-optimized JVM options for Railway
ENV CATALINA_OPTS="-Xmx400m -Xms200m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=32m -XX:+UseG1GC -XX:G1HeapRegionSize=8m -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Remove default webapps and copy our WAR as ROOT
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Create startup script that properly handles Railway's PORT
RUN cat > /start.sh << 'EOF'
#!/bin/bash
set -e

# Get the port from environment, default to 8080
export PORT=${PORT:-8080}

echo "=================================================="
echo "Railway Tomcat Startup"
echo "PORT environment variable: $PORT"
echo "=================================================="

# Update server.xml to use the correct port
echo "Updating Tomcat server.xml to use port $PORT..."
sed -i "s/port=\"8080\"/port=\"$PORT\"/g" /usr/local/tomcat/conf/server.xml

echo "Starting Tomcat on port $PORT..."
exec catalina.sh run
EOF

RUN chmod +x /start.sh

# Default port (Railway will override with environment variable)
EXPOSE 8080

# Health check that works with dynamic ports
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/health || exit 1

# Use our startup script
CMD ["/start.sh"] 