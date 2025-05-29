# Use Eclipse Temurin JDK Alpine image (Railway's recommended approach)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy all project files
COPY . ./

# Make Maven wrapper executable and ensure Unix line endings
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# Build the application and download webapp-runner (Railway's pattern)
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean package

# Verify that both WAR and webapp-runner.jar were created
RUN ls -la target/ && ls -la target/dependency/ && \
    test -f target/aquarium-api.war || (echo "WAR file not found!" && exit 1) && \
    test -f target/dependency/webapp-runner.jar || (echo "webapp-runner.jar not found!" && exit 1)

# Expose port
EXPOSE 8080

# Set environment variable for port
ENV PORT=8080

# Set database URL for Neon PostgreSQL
ENV DATABASE_URL=postgres://neondb_owner:npg_POpns15rGmed@ep-restless-tooth-a4ch55l0-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require

# Run the application using Railway-compatible pattern with proper environment variable expansion
CMD ["sh", "-c", "java -jar target/dependency/webapp-runner.jar --port ${PORT:-8080} target/aquarium-api.war"] 