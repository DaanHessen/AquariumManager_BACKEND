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

# Set memory-optimized JVM options for Railway
ENV CATALINA_OPTS="-Xmx400m -Xms200m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=32m -XX:+UseG1GC -XX:G1HeapRegionSize=8m -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Remove default webapps and copy our WAR as ROOT
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Create startup script to handle PORT environment variable
RUN echo '#!/bin/sh' > /usr/local/tomcat/bin/start-tomcat.sh && \
    echo 'export PORT=${PORT:-8080}' >> /usr/local/tomcat/bin/start-tomcat.sh && \
    echo 'sed -i "s/8080/$PORT/g" /usr/local/tomcat/conf/server.xml' >> /usr/local/tomcat/bin/start-tomcat.sh && \
    echo 'exec /usr/local/tomcat/bin/catalina.sh run' >> /usr/local/tomcat/bin/start-tomcat.sh && \
    chmod +x /usr/local/tomcat/bin/start-tomcat.sh

# Expose port
EXPOSE 8080

# Health check with reduced frequency to save resources
HEALTHCHECK --interval=60s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Run Tomcat
CMD ["/usr/local/tomcat/bin/start-tomcat.sh"] 