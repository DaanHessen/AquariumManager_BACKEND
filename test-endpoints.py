#!/usr/bin/env python3
"""
Simple script to test Railway endpoints after deployment.
Usage: python test-endpoints.py <your-railway-url>
"""
import sys
import requests
import json

def test_endpoint(url, endpoint, expected_status=200):
    """Test an endpoint and return the result"""
    full_url = f"{url.rstrip('/')}{endpoint}"
    try:
        print(f"Testing {full_url}...")
        response = requests.get(full_url, timeout=10)
        print(f"  Status: {response.status_code}")
        
        if response.status_code == expected_status:
            print(f"  ✅ SUCCESS: {endpoint}")
            try:
                json_response = response.json()
                print(f"  Response: {json.dumps(json_response, indent=2)}")
            except json.JSONDecodeError:
                print(f"  Response (text): {response.text[:200]}...")
        else:
            print(f"  ❌ FAILED: Expected {expected_status}, got {response.status_code}")
            print(f"  Response: {response.text[:200]}...")
        
        return response.status_code == expected_status
        
    except requests.RequestException as e:
        print(f"  ❌ ERROR: {str(e)}")
        return False

def main():
    if len(sys.argv) != 2:
        print("Usage: python test-endpoints.py <railway-url>")
        print("Example: python test-endpoints.py https://your-app.railway.app")
        sys.exit(1)
    
    base_url = sys.argv[1]
    print(f"Testing Railway deployment at: {base_url}")
    print("=" * 50)
    
    # Test health endpoints
    health_ok = test_endpoint(base_url, "/health")
    api_health_ok = test_endpoint(base_url, "/api/health")
    
    # Test API root
    api_root_ok = test_endpoint(base_url, "/api/")
    
    print("\n" + "=" * 50)
    print("SUMMARY:")
    print(f"  /health: {'✅' if health_ok else '❌'}")
    print(f"  /api/health: {'✅' if api_health_ok else '❌'}")  
    print(f"  /api/: {'✅' if api_root_ok else '❌'}")
    
    if health_ok:
        print("\n🎉 Railway health check should pass!")
    else:
        print("\n⚠️  Railway health check may fail!")

if __name__ == "__main__":
    main() 