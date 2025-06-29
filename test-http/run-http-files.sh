#!/bin/bash

BASE_URL="http://localhost:8085/api"
TIMESTAMP=$(date +%s)
TEST_EMAIL="${TIMESTAMP}@gmail.com"
TEST_PASSWORD="testpassword123"

echo "

████████╗███████╗███████╗████████╗███████╗
╚══██╔══╝██╔════╝██╔════╝╚══██╔══╝██╔════╝
   ██║   █████╗  ███████╗   ██║   ███████╗
   ██║   ██╔══╝  ╚════██║   ██║   ╚════██║
   ██║   ███████╗███████║   ██║   ███████║
   ╚═╝   ╚══════╝╚══════╝   ╚═╝   ╚══════╝
                                          

"

echo "Registering user: $TEST_EMAIL"
register_response=$(curl -s -X POST "$BASE_URL/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"firstName\": \"Test\", \"lastName\": \"User\", \"email\": \"$TEST_EMAIL\", \"password\": \"$TEST_PASSWORD\"}")

echo "Logging in..."
login_response=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\": \"$TEST_EMAIL\", \"password\": \"$TEST_PASSWORD\"}")

JWT_TOKEN=$(echo $login_response | grep -o '"token":"[^"]*' | grep -o '[^"]*$')

if [[ -z $JWT_TOKEN ]]; then
    echo "Failed to get JWT token"
    exit 1
fi

echo "Got JWT token: Bearer $JWT_TOKEN"
echo ""

for file in auth.http api-info.http aquariums.http accessories.http inhabitants.http ornaments.http; do
    if [[ -f "$file" ]]; then
        sed -i "s|@token = Bearer.*|@token = Bearer $JWT_TOKEN|g" "$file"
    fi
done

echo "🦍🦍🦍🦍🦍🦍🦍🦍🦍🦍"

echo "GET /"
curl -s "$BASE_URL/../" | jq . 2>/dev/null || curl -s "$BASE_URL/../"
echo ""

echo "POST /auth/login (wrong pass)"
curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\": \"$TEST_EMAIL\", \"password\": \"wrongpassword\"}" | jq . 2>/dev/null
echo ""

echo "GET /aquariums"
curl -s "$BASE_URL/aquariums" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

echo "POST /aquariums (first)"
create_response=$(curl -s -X POST "$BASE_URL/aquariums" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "abc", "length": 120.0, "width": 60.0, "height": 80.0, "substrate": "SAND", "waterType": "FRESHWATER", "color": "Black", "description": "yes", "state": "SETUP"}')
echo "$create_response" | jq . 2>/dev/null || echo "$create_response"
AQUARIUM_ID=$(echo "$create_response" | jq -r '.data.id // empty')
echo "DEBUG: Extracted AQUARIUM_ID = '$AQUARIUM_ID'"
echo ""

echo "POST /aquariums (second)"
create_response2=$(curl -s -X POST "$BASE_URL/aquariums" \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "hmm", "length": 150.0, "width": 80.0, "height": 100.0, "substrate": "GRAVEL", "waterType": "SALTWATER", "color": "Clear", "description": "🦍🦍🦍🦍", "state": "SETUP"}')
echo "$create_response2" | jq . 2>/dev/null || echo "$create_response2"
AQUARIUM_ID2=$(echo "$create_response2" | jq -r '.data.id // empty')
echo ""

echo "GET /aquariums (should show created aquariums)"
curl -s "$BASE_URL/aquariums" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $AQUARIUM_ID ]]; then
    echo "GET /aquariums/$AQUARIUM_ID"
    curl -s "$BASE_URL/aquariums/$AQUARIUM_ID" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
    echo ""
    
    echo "PUT /aquariums/$AQUARIUM_ID (update)"
    curl -s -X PUT "$BASE_URL/aquariums/$AQUARIUM_ID" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d '{"name": "good", "length": 120.0, "width": 60.0, "height": 80.0, "substrate": "GRAVEL", "waterType": "FRESHWATER", "color": "Green", "description": "🦍🦍🦍🦍", "state": "ACTIVE"}' | jq . 2>/dev/null
    echo ""
fi

echo "GET /accessories"
curl -s "$BASE_URL/accessories" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $AQUARIUM_ID ]]; then
    echo "POST /accessories (create filter)"
    acc_response=$(curl -s -X POST "$BASE_URL/accessories" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"model\": \"model 1\", \"serialNumber\": \"s3r14lnumb3r\", \"type\": \"Filter\", \"aquariumId\": $AQUARIUM_ID, \"isExternal\": true, \"capacityLiters\": 300, \"color\": \"Black\", \"description\": \"e\"}")
    echo "$acc_response" | jq . 2>/dev/null || echo "$acc_response"
    ACCESSORY_ID=$(echo "$acc_response" | jq -r '.data.id // empty')
    echo ""
    
    echo "POST /accessories (create lighting)"
    curl -s -X POST "$BASE_URL/accessories" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"model\": \"model 2\", \"serialNumber\": \"2000000000\", \"type\": \"Lighting\", \"aquariumId\": $AQUARIUM_ID, \"isLED\": true, \"color\": \"White\", \"description\": \"what?\", \"timeOn\": \"08:00\", \"timeOff\": \"22:00\"}" | jq . 2>/dev/null
    echo ""
fi

echo "GET /accessories"
curl -s "$BASE_URL/accessories" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $ACCESSORY_ID ]]; then
    echo "GET /accessories/$ACCESSORY_ID"
    curl -s "$BASE_URL/accessories/$ACCESSORY_ID" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
    echo ""
fi

echo "GET /inhabitants"
curl -s "$BASE_URL/inhabitants" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $AQUARIUM_ID ]]; then
    echo "POST /inhabitants (fish)"
    inh_response=$(curl -s -X POST "$BASE_URL/inhabitants" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"name\": \"Nemo\", \"species\": \"Clownfish\", \"color\": \"Orange and White\", \"count\": 2, \"isSchooling\": false, \"waterType\": \"SALTWATER\", \"description\": \"Beautiful clownfish pair\", \"aquariumId\": $AQUARIUM_ID, \"type\": \"Fish\", \"isAggressiveEater\": false, \"requiresSpecialFood\": false, \"isSnailEater\": false, \"age\": 1, \"gender\": \"Mixed\", \"phLevel\": 8.2, \"temperature\": 26.0, \"tankSize\": 200.0, \"aggressionLevel\": 2, \"saltTolerance\": 1.025}")
    echo "$inh_response" | jq . 2>/dev/null || echo "$inh_response"
    INHABITANT_ID=$(echo "$inh_response" | jq -r '.data.id // empty')
    echo ""
    
    echo "POST /inhabitants (plant)"
    curl -s -X POST "$BASE_URL/inhabitants" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"name\": \"Amazon Sword\", \"species\": \"Echinodorus bleheri\", \"color\": \"Green\", \"count\": 3, \"isSchooling\": false, \"waterType\": \"FRESHWATER\", \"description\": \"Large aquatic plant for background\", \"aquariumId\": $AQUARIUM_ID, \"type\": \"Plant\", \"age\": 1, \"gender\": \"N/A\", \"phLevel\": 6.8, \"temperature\": 24.0, \"tankSize\": 150.0, \"aggressionLevel\": 0, \"saltTolerance\": 0.0}" | jq . 2>/dev/null
    echo ""
fi

echo "GET /inhabitants"
curl -s "$BASE_URL/inhabitants" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $INHABITANT_ID ]]; then
    echo "GET /inhabitants/$INHABITANT_ID"
    curl -s "$BASE_URL/inhabitants/$INHABITANT_ID" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
    echo ""
fi

echo "GET /ornaments"
curl -s "$BASE_URL/ornaments" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $AQUARIUM_ID ]]; then
    echo "POST /ornaments (castle)"
    orn_response=$(curl -s -X POST "$BASE_URL/ornaments" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"name\": \"Medieval Castle\", \"color\": \"Gray\", \"material\": \"Resin\", \"description\": \"Large decorative castle with multiple towers\", \"isAirPumpCompatible\": true, \"aquariumId\": $AQUARIUM_ID}")
    echo "$orn_response" | jq . 2>/dev/null || echo "$orn_response"
    ORNAMENT_ID=$(echo "$orn_response" | jq -r '.data.id // empty')
    echo ""
    
    echo "POST /ornaments (driftwood)"
    curl -s -X POST "$BASE_URL/ornaments" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "{\"name\": \"Malaysian Driftwood\", \"color\": \"Brown\", \"material\": \"Wood\", \"description\": \"Natural driftwood piece for aquascaping\", \"isAirPumpCompatible\": false, \"aquariumId\": $AQUARIUM_ID}" | jq . 2>/dev/null
    echo ""
fi

echo "GET /ornaments"
curl -s "$BASE_URL/ornaments" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
echo ""

if [[ -n $ORNAMENT_ID ]]; then
    echo "GET /ornaments/$ORNAMENT_ID"
    curl -s "$BASE_URL/ornaments/$ORNAMENT_ID" -H "Authorization: Bearer $JWT_TOKEN" | jq . 2>/dev/null
    echo ""
fi

echo "🦍🦍TESTS COMPLETED🦍🦍"