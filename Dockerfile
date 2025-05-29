# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper files first
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Make Maven wrapper executable
RUN chmod +x mvnw

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies using Maven wrapper (unset MAVEN_CONFIG to avoid conflicts)
RUN unset MAVEN_CONFIG && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application using Maven wrapper (unset MAVEN_CONFIG to avoid conflicts)
RUN unset MAVEN_CONFIG && ./mvnw clean package -DskipTests

# Verify that webapp-runner.jar was created
RUN ls -la target/dependency/ && \
    test -f target/dependency/webapp-runner.jar || (echo "webapp-runner.jar not found!" && exit 1)

# Use OpenJDK for runtime
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built WAR file and webapp-runner
COPY --from=build /app/target/aquarium-api.war ./app.war
COPY --from=build /app/target/dependency/webapp-runner.jar ./webapp-runner.jar

# Verify files were copied correctly
RUN ls -la . && \
    test -f webapp-runner.jar || (echo "webapp-runner.jar not found in final image!" && exit 1) && \
    test -f app.war || (echo "app.war not found in final image!" && exit 1)

# Expose port
EXPOSE 8080

# Set environment variable for port
ENV PORT=8080

# Set database URL for Neon PostgreSQL
ENV DATABASE_URL=postgres://neondb_owner:npg_POpns15rGmed@ep-restless-tooth-a4ch55l0-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require

# Run the application
CMD ["sh", "-c", "java -jar webapp-runner.jar --port ${PORT:-8080} app.war"] 