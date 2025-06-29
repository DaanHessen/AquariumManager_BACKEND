#!/bin/bash

# Configuration
BASE_URL="http://localhost:8085/api"
TIMESTAMP=$(date +%s)
TEST_EMAIL="testuser${TIMESTAMP}@example.com"
TEST_PASSWORD="testpassword123"
TEST_FIRST_NAME="TestUser${TIMESTAMP}"
TEST_LAST_NAME="ApiTest${TIMESTAMP}"

# Function to make HTTP request and show response
make_request() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo "=== $description ==="
    echo "Request: $method $endpoint"
    
    if [[ -n $data ]]; then
        echo "Data: $data"
        if [[ -n $JWT_TOKEN ]]; then
            curl -s -X $method "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $JWT_TOKEN" \
                -H "Content-Type: application/json" \
                -d "$data" | jq . 2>/dev/null || curl -s -X $method "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $JWT_TOKEN" \
                -H "Content-Type: application/json" \
                -d "$data"
        else
            curl -s -X $method "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -d "$data" | jq . 2>/dev/null || curl -s -X $method "$BASE_URL$endpoint" \
                -H "Content-Type: application/json" \
                -d "$data"
        fi
    else
        if [[ -n $JWT_TOKEN ]]; then
            curl -s -X $method "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null || curl -s -X $method "$BASE_URL$endpoint" \
                -H "Authorization: Bearer $JWT_TOKEN"
        else
            curl -s -X $method "$BASE_URL$endpoint" | jq . 2>/dev/null || curl -s -X $method "$BASE_URL$endpoint"
        fi
    fi
    echo
    echo
}

# Register user
echo "Registering user..."
register_response=$(curl -s -w "HTTPSTATUS:%{http_code}" \
    -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d "{
        \"firstName\": \"$TEST_FIRST_NAME\",
        \"lastName\": \"$TEST_LAST_NAME\",
        \"email\": \"$TEST_EMAIL\",
        \"password\": \"$TEST_PASSWORD\"
    }")

register_http_code=$(echo $register_response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
register_body=$(echo $register_response | sed -e 's/HTTPSTATUS\:.*//g')

if [[ $register_http_code -eq 201 ]]; then
    echo "User registered successfully: $TEST_EMAIL"
else
    echo "Registration failed (HTTP $register_http_code): $register_body"
    exit 1
fi
echo

# Login and get JWT token
echo "Logging in..."
login_response=$(curl -s -w "HTTPSTATUS:%{http_code}" \
    -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{
        \"email\": \"$TEST_EMAIL\",
        \"password\": \"$TEST_PASSWORD\"
    }")

login_http_code=$(echo $login_response | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')
login_body=$(echo $login_response | sed -e 's/HTTPSTATUS\:.*//g')

if [[ $login_http_code -eq 200 ]]; then
    JWT_TOKEN=$(echo $login_body | grep -o '"token":"[^"]*' | grep -o '[^"]*$')
    if [[ -n $JWT_TOKEN ]]; then
        echo "Login successful, got JWT token"
    else
        echo "Login successful but could not extract token"
        exit 1
    fi
else
    echo "Login failed (HTTP $login_http_code): $login_body"
    exit 1
fi
echo

# API Info Tests
make_request "GET" "" "" "API Root"
make_request "GET" "/api" "" "API Info"

# Auth Tests  
make_request "POST" "/auth/login" "{\"email\": \"$TEST_EMAIL\", \"password\": \"wrongpassword\"}" "Login with wrong password"

# Aquarium Tests
make_request "GET" "/aquariums" "" "Get all aquariums"
make_request "POST" "/aquariums" "{\"name\": \"Test Aquarium\", \"length\": 100.0, \"width\": 50.0, \"height\": 60.0, \"substrate\": \"SAND\", \"waterType\": \"FRESHWATER\", \"color\": \"Blue\", \"description\": \"Test aquarium\", \"state\": \"SETUP\"}" "Create aquarium"
make_request "GET" "/aquariums/1" "" "Get aquarium by ID"
make_request "PUT" "/aquariums/1" "{\"name\": \"Updated Aquarium\", \"length\": 120.0, \"width\": 60.0, \"height\": 80.0, \"substrate\": \"GRAVEL\", \"waterType\": \"FRESHWATER\", \"color\": \"Green\", \"description\": \"Updated aquarium\", \"state\": \"ACTIVE\"}" "Update aquarium"

# Accessories Tests
make_request "GET" "/accessories" "" "Get all accessories"
make_request "POST" "/accessories" "{\"model\": \"AquaClear 70\", \"serialNumber\": \"AC70-2024-001\", \"type\": \"Filter\", \"aquariumId\": 1, \"isExternal\": true, \"capacityLiters\": 300, \"color\": \"Black\", \"description\": \"300 GPH flow rate filter\"}" "Create filter accessory"
make_request "POST" "/accessories" "{\"model\": \"LED Pro Marine\", \"serialNumber\": \"LPM-48-2024-002\", \"type\": \"Lighting\", \"aquariumId\": 1, \"isLED\": true, \"color\": \"White\", \"description\": \"Full spectrum LED\", \"timeOn\": \"08:00\", \"timeOff\": \"22:00\"}" "Create lighting accessory"
make_request "GET" "/accessories/1" "" "Get accessory by ID"

# Inhabitants Tests
make_request "GET" "/inhabitants" "" "Get all inhabitants"
make_request "POST" "/inhabitants" "{\"name\": \"Nemo\", \"species\": \"Clownfish\", \"color\": \"Orange\", \"count\": 2, \"isSchooling\": false, \"waterType\": \"SALTWATER\", \"description\": \"Clownfish pair\", \"aquariumId\": 1, \"type\": \"Fish\", \"age\": 1, \"gender\": \"Mixed\", \"phLevel\": 8.2, \"temperature\": 26.0, \"tankSize\": 200.0, \"aggressionLevel\": 2, \"saltTolerance\": 1.025}" "Create fish inhabitant"
make_request "POST" "/inhabitants" "{\"name\": \"Amazon Sword\", \"species\": \"Echinodorus bleheri\", \"color\": \"Green\", \"count\": 3, \"waterType\": \"FRESHWATER\", \"description\": \"Aquatic plant\", \"aquariumId\": 1, \"type\": \"Plant\", \"age\": 1, \"phLevel\": 6.8, \"temperature\": 24.0}" "Create plant inhabitant"
make_request "GET" "/inhabitants/1" "" "Get inhabitant by ID"

# Ornaments Tests
make_request "GET" "/ornaments" "" "Get all ornaments"
make_request "POST" "/ornaments" "{\"name\": \"Medieval Castle\", \"color\": \"Gray\", \"material\": \"Resin\", \"description\": \"Decorative castle\", \"isAirPumpCompatible\": true, \"aquariumId\": 1}" "Create castle ornament"
make_request "POST" "/ornaments" "{\"name\": \"Driftwood\", \"color\": \"Brown\", \"material\": \"Wood\", \"description\": \"Natural driftwood\", \"isAirPumpCompatible\": false, \"aquariumId\": 1}" "Create driftwood ornament"
make_request "GET" "/ornaments/1" "" "Get ornament by ID"

echo "All tests completed." 