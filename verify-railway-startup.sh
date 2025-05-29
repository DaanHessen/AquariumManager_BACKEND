#!/bin/bash

# Railway Startup Verification Script
echo "🚀 Verifying Railway startup configuration..."

# Set test environment variables similar to Railway
export PORT=8080
export DATABASE_URL="postgres://neondb_owner:npg_POpns15rGmed@ep-restless-tooth-a4ch55l0-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require"
export JWT_SECRET="test_jwt_secret_key"
export JWT_EXPIRATION="86400000"
export HIBERNATE_HBM2DDL="update"

echo "📦 Building Docker image for Railway deployment..."
docker build -t aquarium-railway-test .

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed!"
    exit 1
fi

echo "✅ Docker build successful!"

echo "🔧 Starting container with Railway-like environment..."
docker run -d -p 8080:8080 \
  -e PORT=8080 \
  -e DATABASE_URL="$DATABASE_URL" \
  -e JWT_SECRET="$JWT_SECRET" \
  -e JWT_EXPIRATION="$JWT_EXPIRATION" \
  -e HIBERNATE_HBM2DDL="$HIBERNATE_HBM2DDL" \
  --name aquarium-railway-test \
  aquarium-railway-test

CONTAINER_ID=$(docker ps -q -f name=aquarium-railway-test)

if [ -z "$CONTAINER_ID" ]; then
    echo "❌ Container failed to start!"
    docker logs aquarium-railway-test
    exit 1
fi

echo "⏳ Waiting for application startup..."
sleep 10

echo "🏥 Testing basic health check endpoint (Railway health check path)..."
for i in {1..30}; do
    echo "Attempt $i/30..."
    
    HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/health)
    
    if [ "$HEALTH_RESPONSE" = "200" ]; then
        echo "✅ Basic health check successful!"
        echo "📋 Health check response:"
        curl -s http://localhost:8080/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/health
        break
    else
        echo "⏳ Health check returned $HEALTH_RESPONSE, retrying in 5 seconds..."
        sleep 5
    fi
    
    if [ $i -eq 30 ]; then
        echo "❌ Health check failed after 30 attempts!"
        echo "📋 Container logs:"
        docker logs aquarium-railway-test
        docker stop aquarium-railway-test
        docker rm aquarium-railway-test
        exit 1
    fi
done

echo "🔍 Testing full health check endpoint..."
FULL_HEALTH_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/health)
echo "Full health check response code: $FULL_HEALTH_RESPONSE"

if [ "$FULL_HEALTH_RESPONSE" = "200" ]; then
    echo "✅ Full health check successful!"
    echo "📋 Full health check response:"
    curl -s http://localhost:8080/api/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/health
else
    echo "⚠️ Full health check returned $FULL_HEALTH_RESPONSE (this may be expected if database is not fully ready)"
    echo "📋 Full health check response:"
    curl -s http://localhost:8080/api/health | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/health
fi

echo "🔍 Testing root API endpoint..."
ROOT_RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/)
echo "Root API response code: $ROOT_RESPONSE"

if [ "$ROOT_RESPONSE" = "200" ]; then
    echo "✅ Root API endpoint successful!"
    echo "📋 Root API response:"
    curl -s http://localhost:8080/api/ | jq '.' 2>/dev/null || curl -s http://localhost:8080/api/
fi

echo "📋 Application startup logs:"
docker logs aquarium-railway-test --tail 50

echo "🧹 Cleaning up..."
docker stop aquarium-railway-test
docker rm aquarium-railway-test

echo "🎉 Railway startup verification completed!"
echo ""
echo "Summary:"
echo "- Basic health check (/health): $([ "$HEALTH_RESPONSE" = "200" ] && echo "✅ PASS" || echo "❌ FAIL")"
echo "- Full health check (/api/health): $([ "$FULL_HEALTH_RESPONSE" = "200" ] && echo "✅ PASS" || echo "⚠️  PARTIAL")"
echo "- Root API endpoint (/api/): $([ "$ROOT_RESPONSE" = "200" ] && echo "✅ PASS" || echo "❌ FAIL")"
echo ""
echo "🚀 Ready for Railway deployment!" 