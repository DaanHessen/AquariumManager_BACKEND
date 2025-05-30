# Use OpenJDK 17 Alpine for smaller memory footprint
FROM openjdk:17-jdk-alpine

# Set memory-optimized JVM options for Railway
ENV JAVA_OPTS="-Xmx400m -Xms200m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=32m -XX:+UseG1GC -XX:G1HeapRegionSize=8m -XX:+UseStringDeduplication -XX:+DisableExplicitGC -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Create app directory and user for security
RUN addgroup -g 1001 -S appgroup && adduser -u 1001 -S appuser -G appgroup
WORKDIR /app

# Copy WAR file (built by Maven)
COPY target/*.war app.war

# Change ownership to app user
RUN chown -R appuser:appgroup /app
USER appuser

# Expose port
EXPOSE 8080

# Health check with reduced frequency to save resources
HEALTHCHECK --interval=60s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/health || exit 1

# Run with optimized settings
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.war"] 