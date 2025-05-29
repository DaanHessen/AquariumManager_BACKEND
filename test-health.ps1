try {
    Write-Host "Testing health endpoint..."
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -Method Get -UseBasicParsing -TimeoutSec 30
    Write-Host "Status Code: $($response.StatusCode)"
    Write-Host "Response Body:"
    Write-Host $response.Content
    Write-Host "SUCCESS: Health endpoint is working!"
} catch {
    Write-Host "ERROR: $($_.Exception.Message)"
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.Value__)"
} 