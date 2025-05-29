#!/bin/bash

# Test script to verify Neon PostgreSQL connection
echo "Testing Neon PostgreSQL connection..."

# Set the DATABASE_URL (replace with your actual connection string)
export DATABASE_URL="postgres://neondb_owner:npg_POpns15rGmed@ep-restless-tooth-a4ch55l0-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require"

# Build the application
echo "Building application..."
./mvnw clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    
    # Start the application in background
    echo "Starting application..."
    java -jar target/dependency/webapp-runner.jar --port 8080 target/aquarium-api.war &
    APP_PID=$!
    
    # Wait for application to start
    echo "Waiting for application to start..."
    sleep 15
    
    # Test basic health check
    echo "Testing basic health check..."
    BASIC_HEALTH=$(curl -s http://localhost:8080/health)
    echo "Basic health response: $BASIC_HEALTH"
    
    # Test database health check
    echo "Testing database health check..."
    DB_HEALTH=$(curl -s http://localhost:8080/api/health)
    echo "Database health response: $DB_HEALTH"
    
    # Check if database health is successful
    if echo "$DB_HEALTH" | grep -q '"status":"success"'; then
        echo "✅ Database connection successful!"
    else
        echo "❌ Database connection failed!"
    fi
    
    # Stop the application
    kill $APP_PID
    echo "Application stopped."
else
    echo "❌ Build failed!"
    exit 1
fi 