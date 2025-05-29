# Aquarium Manager API Documentation

## Overview

The Aquarium Manager API is a RESTful web service for managing aquariums, their inhabitants, accessories, and ornaments. The API provides comprehensive functionality for aquarium enthusiasts to track and manage their aquatic setups.

**Base URL:** `https://your-domain.com/api`

**Version:** 1.0.0 beta

## Authentication

### Authentication Method
The API uses JWT (JSON Web Token) based authentication. After successful login or registration, you'll receive a token that must be included in subsequent requests.

### Headers Required
For authenticated endpoints, include the following header:
```
Authorization: Bearer <your-jwt-token>
```

### Content Type
All requests should include:
```
Content-Type: application/json
```

## Global Response Format

All API responses follow a consistent wrapper format:

```json
{
  "status": "success|error",
  "data": <response_data>,
  "timestamp": 1234567890123,
  "message": "Optional message"
}
```

## Data Models

### Enums

#### AquariumState
- `SETUP` - Aquarium is being set up
- `RUNNING` - Aquarium is operational
- `MAINTENANCE` - Aquarium is under maintenance
- `INACTIVE` - Aquarium is not in use

#### SubstrateType
- `SAND` - Sand substrate
- `GRAVEL` - Gravel substrate
- `SOIL` - Soil substrate

#### WaterType
- `FRESH` - Freshwater
- `SALT` - Saltwater

## API Endpoints

### Health & Information

#### Get API Information
- **Method:** `GET`
- **Endpoint:** `/`
- **Authentication:** None
- **Description:** Returns basic API information and available endpoints

**Response:**
```json
{
  "status": "success",
  "data": {
    "name": "Aquarium API",
    "version": "1.0.0 beta",
    "endpoints": {
      "aquariums": "/api/aquariums",
      "inhabitants": "/api/inhabitants",
      "authentication": "/api/auth",
      "health-detailed": "/api/health",
      "health-basic": "/health"
    },
    "notes": {
      "health-basic": "Simple health check for Railway deployment",
      "health-detailed": "Detailed health check including database connectivity"
    }
  },
  "timestamp": 1234567890123,
  "message": null
}
```

#### Health Check (Detailed)
- **Method:** `GET`
- **Endpoint:** `/health`
- **Authentication:** None
- **Description:** Detailed health check including database connectivity

**Response (Healthy):**
```json
{
  "status": "success",
  "data": {
    "status": "UP",
    "timestamp": 1234567890123,
    "service": "Aquarium API - Detailed Health Check",
    "environment": {
      "DATABASE_URL_present": true,
      "DB_HOST": "present",
      "DB_PORT": "present",
      "DB_NAME": "present",
      "DB_USER": "present",
      "DB_PASSWORD": "present",
      "PORT": "8080"
    },
    "database": "UP"
  },
  "timestamp": 1234567890123,
  "message": "Service is healthy"
}
```

### Authentication

#### Register
- **Method:** `POST`
- **Endpoint:** `/auth/register`
- **Authentication:** None
- **Description:** Register a new user account

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "ownerId": 1,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": 1234567890123,
  "message": "Registration successful"
}
```

#### Login
- **Method:** `POST`
- **Endpoint:** `/auth/login`
- **Authentication:** None
- **Description:** Authenticate user and receive access token

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "ownerId": 1,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": 1234567890123,
  "message": "Login successful"
}
```

### Aquariums

#### Get All Aquariums
- **Method:** `GET`
- **Endpoint:** `/aquariums`
- **Authentication:** Required (Bearer token)
- **Description:** Get all aquariums owned by the authenticated user

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "My First Tank",
      "length": 60.0,
      "width": 30.0,
      "height": 40.0,
      "volumeInLiters": 72.0,
      "substrate": "GRAVEL",
      "waterType": "FRESH",
      "state": "RUNNING",
      "ownerId": 1,
      "ownerEmail": "john.doe@example.com",
      "inhabitants": [],
      "accessories": [],
      "ornaments": [],
      "color": "Blue",
      "description": "My beautiful freshwater tank",
      "dateCreated": "2024-01-15T10:30:00"
    }
  ],
  "timestamp": 1234567890123,
  "message": "Aquariums fetched successfully"
}
```

#### Get Aquarium by ID
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}`
- **Authentication:** None
- **Description:** Get detailed information about a specific aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "My First Tank",
    "length": 60.0,
    "width": 30.0,
    "height": 40.0,
    "volumeInLiters": 72.0,
    "substrate": "GRAVEL",
    "waterType": "FRESH",
    "state": "RUNNING",
    "ownerId": 1,
    "ownerEmail": "john.doe@example.com",
    "inhabitants": [
      {
        "id": 1,
        "species": "Neon Tetra",
        "color": "Blue and Red",
        "description": "Small schooling fish",
        "dateCreated": "2024-01-15T11:00:00",
        "count": 10,
        "isSchooling": true,
        "waterType": "FRESH",
        "aquariumId": 1,
        "type": "Fish",
        "isAggressiveEater": false,
        "requiresSpecialFood": false,
        "isSnailEater": false
      }
    ],
    "accessories": [],
    "ornaments": [],
    "color": "Blue",
    "description": "My beautiful freshwater tank",
    "dateCreated": "2024-01-15T10:30:00"
  },
  "timestamp": 1234567890123,
  "message": "Aquarium fetched successfully"
}
```

#### Get Aquarium Detail by ID
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}/detail`
- **Authentication:** None
- **Description:** Get detailed aquarium information with inhabitants

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:** Same as "Get Aquarium by ID"

#### Create Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new aquarium

**Request Body:**
```json
{
  "name": "My New Tank",
  "length": 80.0,
  "width": 35.0,
  "height": 45.0,
  "substrate": "SAND",
  "waterType": "SALT",
  "color": "Black",
  "description": "A beautiful saltwater setup",
  "state": "SETUP"
}
```

**Response:**
```json
{
  "status": "success",
  "data": {
    "id": 2,
    "name": "My New Tank",
    "length": 80.0,
    "width": 35.0,
    "height": 45.0,
    "volumeInLiters": 126.0,
    "substrate": "SAND",
    "waterType": "SALT",
    "state": "SETUP",
    "ownerId": 1,
    "ownerEmail": "john.doe@example.com",
    "inhabitants": [],
    "accessories": [],
    "ornaments": [],
    "color": "Black",
    "description": "A beautiful saltwater setup",
    "dateCreated": "2024-01-15T12:00:00"
  },
  "timestamp": 1234567890123,
  "message": "Aquarium created successfully"
}
```

#### Update Aquarium
- **Method:** `PUT`
- **Endpoint:** `/aquariums/{id}`
- **Authentication:** Required (Bearer token)
- **Description:** Update an existing aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Request Body:** Same as Create Aquarium

**Response:** Updated aquarium object

#### Delete Aquarium
- **Method:** `DELETE`
- **Endpoint:** `/aquariums/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Delete an aquarium (owner only)

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "aquariumId": 1
  },
  "timestamp": 1234567890123,
  "message": "Aquarium deleted successfully"
}
```

#### Add Accessory to Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums/{aquariumId}/accessories/{accessoryId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Add an accessory to an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `accessoryId` (Long) - Accessory ID

**Request Body:**
```json
{
  "customProperty": "value"
}
```

**Response:** Updated aquarium object with accessories

#### Remove Accessory from Aquarium
- **Method:** `DELETE`
- **Endpoint:** `/aquariums/{aquariumId}/accessories/{accessoryId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Remove an accessory from an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `accessoryId` (Long) - Accessory ID

**Response:** Updated aquarium object

#### Add Ornament to Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums/{aquariumId}/ornaments/{ornamentId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Add an ornament to an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `ornamentId` (Long) - Ornament ID

**Request Body:**
```json
{
  "customProperty": "value"
}
```

**Response:** Updated aquarium object with ornaments

#### Remove Ornament from Aquarium
- **Method:** `DELETE`
- **Endpoint:** `/aquariums/{aquariumId}/ornaments/{ornamentId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Remove an ornament from an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `ornamentId` (Long) - Ornament ID

**Response:** Updated aquarium object

#### Add Inhabitant to Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums/{aquariumId}/inhabitants/{inhabitantId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Add an inhabitant to an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `inhabitantId` (Long) - Inhabitant ID

**Request Body:**
```json
{
  "customProperty": "value"
}
```

**Response:** Updated aquarium object with inhabitants

#### Remove Inhabitant from Aquarium
- **Method:** `DELETE`
- **Endpoint:** `/aquariums/{aquariumId}/inhabitants/{inhabitantId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Remove an inhabitant from an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `inhabitantId` (Long) - Inhabitant ID

**Response:** Updated aquarium object

### Accessories

#### Get All Accessories
- **Method:** `GET`
- **Endpoint:** `/accessories`
- **Authentication:** Required (Bearer token)
- **Description:** Get all accessories owned by the authenticated user

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "model": "AquaClear 50",
      "serialNumber": "AC50-001",
      "type": "Filter",
      "isExternal": false,
      "capacityLiters": 200,
      "isLed": false,
      "turnOnTime": "08:00:00",
      "turnOffTime": "20:00:00",
      "minTemperature": 22.0,
      "maxTemperature": 28.0,
      "currentTemperature": 25.0,
      "color": "Black",
      "description": "High-quality filter system",
      "dateCreated": "2024-01-15T10:45:00"
    }
  ],
  "timestamp": 1234567890123,
  "message": "Accessories fetched successfully"
}
```

#### Get Accessory by ID
- **Method:** `GET`
- **Endpoint:** `/accessories/{id}`
- **Authentication:** None
- **Description:** Get detailed information about a specific accessory

**Path Parameters:**
- `id` (Long) - Accessory ID

**Response:** Single accessory object

#### Get Accessories by Aquarium
- **Method:** `GET`
- **Endpoint:** `/accessories/byAquarium/{aquariumId}`
- **Authentication:** None
- **Description:** Get all accessories for a specific aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID

**Response:** Array of accessory objects

#### Create Accessory
- **Method:** `POST`
- **Endpoint:** `/accessories`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new accessory

**Request Body:**
```json
{
  "model": "AquaClear 70",
  "serialNumber": "AC70-002",
  "type": "Filter",
  "aquariumId": 1,
  "isExternal": false,
  "capacityInLiters": 300,
  "isLED": false,
  "color": "Black",
  "description": "Powerful filtration system",
  "timeOn": "07:00",
  "timeOff": "21:00",
  "minTemperature": 20.0,
  "maxTemperature": 30.0,
  "currentTemperature": 24.0
}
```

**Response:** Created accessory object

#### Update Accessory
- **Method:** `PUT`
- **Endpoint:** `/accessories/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Update an existing accessory

**Path Parameters:**
- `id` (Long) - Accessory ID

**Request Body:** Same as Create Accessory

**Response:** Updated accessory object

#### Delete Accessory
- **Method:** `DELETE`
- **Endpoint:** `/accessories/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Delete an accessory (owner only)

**Path Parameters:**
- `id` (Long) - Accessory ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "accessoryId": 1
  },
  "timestamp": 1234567890123,
  "message": "Accessory deleted successfully"
}
```

### Inhabitants

#### Get All Inhabitants
- **Method:** `GET`
- **Endpoint:** `/inhabitants`
- **Authentication:** Required (Bearer token)
- **Description:** Get all inhabitants owned by the authenticated user

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "species": "Neon Tetra",
      "color": "Blue and Red",
      "description": "Small schooling fish",
      "dateCreated": "2024-01-15T11:00:00",
      "count": 10,
      "isSchooling": true,
      "waterType": "FRESH",
      "aquariumId": 1,
      "type": "Fish",
      "isAggressiveEater": false,
      "requiresSpecialFood": false,
      "isSnailEater": false
    }
  ],
  "timestamp": 1234567890123,
  "message": "Inhabitants fetched successfully"
}
```

#### Get Inhabitant by ID
- **Method:** `GET`
- **Endpoint:** `/inhabitants/{id}`
- **Authentication:** None
- **Description:** Get detailed information about a specific inhabitant

**Path Parameters:**
- `id` (Long) - Inhabitant ID

**Response:** Single inhabitant object

#### Get Inhabitants by Aquarium
- **Method:** `GET`
- **Endpoint:** `/inhabitants/byAquarium/{aquariumId}`
- **Authentication:** None
- **Description:** Get all inhabitants for a specific aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID

**Response:** Array of inhabitant objects

#### Create Inhabitant
- **Method:** `POST`
- **Endpoint:** `/inhabitants`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new inhabitant

**Request Body:**
```json
{
  "species": "Angelfish",
  "color": "Silver",
  "description": "Beautiful freshwater angelfish",
  "count": 2,
  "isSchooling": false,
  "waterType": "FRESH",
  "type": "Fish",
  "aquariumId": 1,
  "isAggressiveEater": true,
  "requiresSpecialFood": false,
  "isSnailEater": false,
  "name": "Angel Pair"
}
```

**Response:** Created inhabitant object

#### Update Inhabitant
- **Method:** `PUT`
- **Endpoint:** `/inhabitants/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Update an existing inhabitant

**Path Parameters:**
- `id` (Long) - Inhabitant ID

**Request Body:** Same as Create Inhabitant

**Response:** Updated inhabitant object

#### Delete Inhabitant
- **Method:** `DELETE`
- **Endpoint:** `/inhabitants/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Delete an inhabitant (owner only)

**Path Parameters:**
- `id` (Long) - Inhabitant ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "inhabitantId": 1
  },
  "timestamp": 1234567890123,
  "message": "Inhabitant deleted successfully"
}
```

### Ornaments

#### Get All Ornaments
- **Method:** `GET`
- **Endpoint:** `/ornaments`
- **Authentication:** Required (Bearer token)
- **Description:** Get all ornaments owned by the authenticated user

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Driftwood",
      "color": "Brown",
      "material": "Wood",
      "description": "Natural driftwood piece",
      "dateCreated": "2024-01-15T11:15:00",
      "isAirPumpCompatible": false
    }
  ],
  "timestamp": 1234567890123,
  "message": "Ornaments fetched successfully"
}
```

#### Get Ornament by ID
- **Method:** `GET`
- **Endpoint:** `/ornaments/{id}`
- **Authentication:** None
- **Description:** Get detailed information about a specific ornament

**Path Parameters:**
- `id` (Long) - Ornament ID

**Response:** Single ornament object

#### Get Ornaments by Aquarium
- **Method:** `GET`
- **Endpoint:** `/ornaments/byAquarium/{aquariumId}`
- **Authentication:** None
- **Description:** Get all ornaments for a specific aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID

**Response:** Array of ornament objects

#### Create Ornament
- **Method:** `POST`
- **Endpoint:** `/ornaments`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new ornament

**Request Body:**
```json
{
  "name": "Castle Decoration",
  "color": "Gray",
  "material": "Ceramic",
  "description": "Medieval castle aquarium decoration",
  "supportsAirPump": true,
  "aquariumId": 1
}
```

**Response:** Created ornament object

#### Update Ornament
- **Method:** `PUT`
- **Endpoint:** `/ornaments/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Update an existing ornament

**Path Parameters:**
- `id` (Long) - Ornament ID

**Request Body:** Same as Create Ornament

**Response:** Updated ornament object

#### Delete Ornament
- **Method:** `DELETE`
- **Endpoint:** `/ornaments/{id}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Delete an ornament (owner only)

**Path Parameters:**
- `id` (Long) - Ornament ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "ornamentId": 1
  },
  "timestamp": 1234567890123,
  "message": "Ornament deleted successfully"
}
```

## Error Handling

### Error Response Format

All errors follow a consistent format:

```json
{
  "status": "error",
  "data": {
    "path": "/api/aquariums/999",
    "code": 404
  },
  "timestamp": 1234567890123,
  "message": "Aquarium with id 999 not found"
}
```

### Common HTTP Status Codes

- **200 OK** - Request successful
- **201 Created** - Resource created successfully
- **400 Bad Request** - Invalid request data
- **401 Unauthorized** - Authentication required or invalid token
- **403 Forbidden** - Access denied (ownership required)
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server error

### Authentication Errors

#### Missing Authorization Header
```json
{
  "status": "error",
  "data": {
    "path": "/api/aquariums",
    "code": 401
  },
  "timestamp": 1234567890123,
  "message": "Authorization header must be provided"
}
```

#### Invalid Token
```json
{
  "status": "error",
  "data": {
    "path": "/api/aquariums",
    "code": 401
  },
  "timestamp": 1234567890123,
  "message": "Invalid token"
}
```

## CORS Configuration

The API supports Cross-Origin Resource Sharing (CORS) with the following configuration:

- **Allowed Origins:** 
  - `https://aquarium-manager-frontend.vercel.app`
  - `localhost` and `127.0.0.1` (development)
  - `*.vercel.app` (Vercel deployments)
  - `*.railway.app` (Railway deployments)

- **Allowed Methods:** `GET, POST, PUT, DELETE, OPTIONS, PATCH`
- **Allowed Headers:** `Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control, Pragma`
- **Credentials:** Supported

## Rate Limiting

Currently, no rate limiting is implemented. This may be added in future versions.

## Versioning

The API is currently in version 1.0.0 beta. Future versions will maintain backward compatibility where possible.

## Support

For API support or questions, please refer to the project documentation or contact the development team. 