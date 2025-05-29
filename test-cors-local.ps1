# Test script to verify CORS and API functionality locally
Write-Host "🧪 Testing Local API with CORS and LazyInitializationException Fix" -ForegroundColor Cyan

# Test basic health check
Write-Host "`n1. Testing Basic Health Check..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-WebRequest -Uri "http://localhost:8080/health" -Method Get -UseBasicParsing -TimeoutSec 10
    Write-Host "✅ Basic Health Check: Status $($healthResponse.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($healthResponse.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Basic Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test detailed health check
Write-Host "`n2. Testing Detailed Health Check..." -ForegroundColor Yellow
try {
    $detailedHealthResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/health" -Method Get -UseBasicParsing -TimeoutSec 10
    Write-Host "✅ Detailed Health Check: Status $($detailedHealthResponse.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($detailedHealthResponse.Content)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Detailed Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test CORS preflight
Write-Host "`n3. Testing CORS Preflight (OPTIONS)..." -ForegroundColor Yellow
try {
    $corsResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/aquariums" -Method OPTIONS -Headers @{
        "Origin" = "https://aquarium-manager-frontend.vercel.app"
        "Access-Control-Request-Method" = "POST"
        "Access-Control-Request-Headers" = "Content-Type, Authorization"
    } -UseBasicParsing -TimeoutSec 10
    Write-Host "✅ CORS Preflight: Status $($corsResponse.StatusCode)" -ForegroundColor Green
    Write-Host "Access-Control-Allow-Origin: $($corsResponse.Headers['Access-Control-Allow-Origin'])" -ForegroundColor Gray
    Write-Host "Access-Control-Allow-Methods: $($corsResponse.Headers['Access-Control-Allow-Methods'])" -ForegroundColor Gray
} catch {
    Write-Host "❌ CORS Preflight Failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test authentication endpoint
Write-Host "`n4. Testing Authentication Endpoint..." -ForegroundColor Yellow
try {
    $authData = @{
        firstName = "Test"
        lastName = "User"
        email = "test@example.com"
        password = "testPassword123"
    } | ConvertTo-Json

    $authResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $authData -ContentType "application/json" -Headers @{
        "Origin" = "https://aquarium-manager-frontend.vercel.app"
    } -UseBasicParsing -TimeoutSec 10
    Write-Host "✅ Auth Registration: Status $($authResponse.StatusCode)" -ForegroundColor Green
    
    # Extract token for next test
    $authResult = $authResponse.Content | ConvertFrom-Json
    $token = $authResult.data.token
    Write-Host "Token received: $($token.Substring(0, 20))..." -ForegroundColor Gray
    
    # Test aquarium creation (the one that was failing)
    Write-Host "`n5. Testing Aquarium Creation (LazyInitializationException Fix)..." -ForegroundColor Yellow
    $aquariumData = @{
        name = "Test Aquarium"
        length = 100.0
        width = 50.0
        height = 60.0
        substrate = "GRAVEL"
        waterType = "FRESH"
        color = "Blue"
        description = "Test aquarium for CORS and LazyInit fix"
    } | ConvertTo-Json

    $aquariumResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/aquariums" -Method POST -Body $aquariumData -ContentType "application/json" -Headers @{
        "Origin" = "https://aquarium-manager-frontend.vercel.app"
        "Authorization" = "Bearer $token"
    } -UseBasicParsing -TimeoutSec 10
    Write-Host "✅ Aquarium Creation: Status $($aquariumResponse.StatusCode)" -ForegroundColor Green
    Write-Host "Response: $($aquariumResponse.Content)" -ForegroundColor Gray
    
} catch {
    Write-Host "❌ Authentication/Aquarium Test Failed: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorStream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorStream)
        $errorContent = $reader.ReadToEnd()
        Write-Host "Error Response: $errorContent" -ForegroundColor Red
    }
}

Write-Host "`n🎯 Local Testing Complete!" -ForegroundColor Cyan
Write-Host "If all tests pass, your fixes are working and ready for Railway deployment." -ForegroundColor Green 