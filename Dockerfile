# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

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

# Run the application
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar webapp-runner.jar --port $PORT app.war"] 