# Aquarium Manager API Documentation

## Overview

The Aquarium Manager API is a RESTful web service for managing aquariums, their inhabitants, accessories, and ornaments. The API provides comprehensive functionality for aquarium enthusiasts to track and manage their aquatic setups, including detailed state history tracking.

**Base URL:** `https://your-domain.com/api`

**Version:** 1.0.0 beta

## Recent Updates (v1.0.0 beta)

### Major Refactoring and New Features
- **Consolidated Endpoints**: All aquarium-related operations are now unified under `/aquariums` with organized sub-paths
- **State History Tracking**: New functionality to track how long aquariums have been in specific states (SETUP, RUNNING, MAINTENANCE, INACTIVE)
- **Improved Architecture**: Reduced code duplication and improved maintainability
- **Enhanced Performance**: Optimized database queries and consolidated service layer

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

### State History Model
```json
{
  "id": 1,
  "state": "RUNNING",
  "startTime": "2024-01-15T10:30:00",
  "endTime": "2024-01-20T14:45:00",
  "durationMinutes": 7455,
  "createdAt": "2024-01-15T10:30:00"
}
```

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
      "authentication": "/api/auth",
      "health-detailed": "/api/health",
      "health-basic": "/health"
    },
    "notes": {
      "health-basic": "Simple health check for Railway deployment",
      "health-detailed": "Detailed health check including database connectivity",
      "consolidated": "All aquarium operations now unified under /aquariums endpoint"
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

## Aquarium Management (Consolidated Endpoints)

All aquarium-related operations are now unified under the `/aquariums` endpoint with organized sub-paths for better maintainability and consistency.

### Core Aquarium Operations

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
      "currentStateStartTime": "2024-01-15T10:30:00",
      "currentStateDurationMinutes": 7455,
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
    "currentStateStartTime": "2024-01-15T10:30:00",
    "currentStateDurationMinutes": 7455,
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

#### Create Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new aquarium (automatically starts state history tracking)

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
    "currentStateStartTime": "2024-01-15T12:00:00",
    "currentStateDurationMinutes": 0,
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
- **Description:** Update an existing aquarium (state changes trigger history tracking)

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

### State History Operations

#### Get Aquarium State History
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}/state-history`
- **Authentication:** None
- **Description:** Get complete state history for an aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "state": "SETUP",
      "startTime": "2024-01-15T10:30:00",
      "endTime": "2024-01-16T09:15:00",
      "durationMinutes": 1365,
      "createdAt": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "state": "RUNNING",
      "startTime": "2024-01-16T09:15:00",
      "endTime": null,
      "durationMinutes": null,
      "createdAt": "2024-01-16T09:15:00"
    }
  ],
  "timestamp": 1234567890123,
  "message": "State history fetched successfully"
}
```

#### Get Current State Duration
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}/current-state-duration`
- **Authentication:** None
- **Description:** Get how long the aquarium has been in its current state

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "aquariumId": 1,
    "currentState": "RUNNING",
    "currentStateStartTime": "2024-01-16T09:15:00",
    "durationMinutes": 7455,
    "durationFormatted": "5 days, 4 hours, 15 minutes"
  },
  "timestamp": 1234567890123,
  "message": "Current state duration fetched successfully"
}
```

### Accessory Management

#### Get Aquarium Accessories
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}/accessories`
- **Authentication:** None
- **Description:** Get all accessories for a specific aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Heater",
      "type": "HEATER",
      "description": "25W aquarium heater",
      "dateCreated": "2024-01-15T11:30:00",
      "aquariumId": 1
    }
  ],
  "timestamp": 1234567890123,
  "message": "Accessories fetched successfully"
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

#### Create Accessory
- **Method:** `POST`
- **Endpoint:** `/aquariums/{id}/accessories`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new accessory for an aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Request Body:**
```json
{
  "name": "New Filter",
  "type": "FILTER",
  "description": "High-efficiency filter system"
}
```

**Response:** Created accessory object

#### Update Accessory
- **Method:** `PUT`
- **Endpoint:** `/aquariums/{aquariumId}/accessories/{accessoryId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Update an accessory

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `accessoryId` (Long) - Accessory ID

**Request Body:** Same as Create Accessory

**Response:** Updated accessory object

### Ornament Management

#### Get Aquarium Ornaments
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}/ornaments`
- **Authentication:** None
- **Description:** Get all ornaments for a specific aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Castle",
      "material": "CERAMIC",
      "description": "Medieval castle decoration",
      "dateCreated": "2024-01-15T11:45:00",
      "aquariumId": 1
    }
  ],
  "timestamp": 1234567890123,
  "message": "Ornaments fetched successfully"
}
```

#### Add Ornament to Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums/{aquariumId}/ornaments/{ornamentId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Add an ornament to an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `ornamentId` (Long) - Ornament ID

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

#### Create Ornament
- **Method:** `POST`
- **Endpoint:** `/aquariums/{id}/ornaments`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new ornament for an aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Request Body:**
```json
{
  "name": "Driftwood",
  "material": "WOOD",
  "description": "Natural driftwood piece"
}
```

**Response:** Created ornament object

#### Update Ornament
- **Method:** `PUT`
- **Endpoint:** `/aquariums/{aquariumId}/ornaments/{ornamentId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Update an ornament

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `ornamentId` (Long) - Ornament ID

**Request Body:** Same as Create Ornament

**Response:** Updated ornament object

### Inhabitant Management

#### Get Aquarium Inhabitants
- **Method:** `GET`
- **Endpoint:** `/aquariums/{id}/inhabitants`
- **Authentication:** None
- **Description:** Get all inhabitants for a specific aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

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

#### Add Inhabitant to Aquarium
- **Method:** `POST`
- **Endpoint:** `/aquariums/{aquariumId}/inhabitants/{inhabitantId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Add an inhabitant to an aquarium

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `inhabitantId` (Long) - Inhabitant ID

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

#### Create Inhabitant
- **Method:** `POST`
- **Endpoint:** `/aquariums/{id}/inhabitants`
- **Authentication:** Required (Bearer token)
- **Description:** Create a new inhabitant for an aquarium

**Path Parameters:**
- `id` (Long) - Aquarium ID

**Request Body:**
```json
{
  "species": "Angelfish",
  "color": "Silver",
  "description": "Beautiful angelfish",
  "count": 2,
  "isSchooling": false,
  "waterType": "FRESH",
  "type": "Fish",
  "isAggressiveEater": false,
  "requiresSpecialFood": false,
  "isSnailEater": false
}
```

**Response:** Created inhabitant object

#### Update Inhabitant
- **Method:** `PUT`
- **Endpoint:** `/aquariums/{aquariumId}/inhabitants/{inhabitantId}`
- **Authentication:** Required (Bearer token + Ownership)
- **Description:** Update an inhabitant

**Path Parameters:**
- `aquariumId` (Long) - Aquarium ID
- `inhabitantId` (Long) - Inhabitant ID

**Request Body:** Same as Create Inhabitant

**Response:** Updated inhabitant object

## Error Responses

### Common Error Codes

#### 400 Bad Request
```json
{
  "status": "error",
  "data": null,
  "timestamp": 1234567890123,
  "message": "Invalid request data"
}
```

#### 401 Unauthorized
```json
{
  "status": "error",
  "data": null,
  "timestamp": 1234567890123,
  "message": "Authentication required"
}
```

#### 403 Forbidden
```json
{
  "status": "error",
  "data": null,
  "timestamp": 1234567890123,
  "message": "Access denied - insufficient permissions"
}
```

#### 404 Not Found
```json
{
  "status": "error",
  "data": null,
  "timestamp": 1234567890123,
  "message": "Resource not found"
}
```

#### 500 Internal Server Error
```json
{
  "status": "error",
  "data": null,
  "timestamp": 1234567890123,
  "message": "Internal server error"
}
```

## Migration Notes

### Endpoint Changes (v1.0.0 beta)

The following endpoints have been consolidated for better organization:

#### Old Structure → New Structure
- `/inhabitants/*` → `/aquariums/{id}/inhabitants/*`
- `/accessories/*` → `/aquariums/{id}/accessories/*`
- `/ornaments/*` → `/aquariums/{id}/ornaments/*`

#### New Endpoints Added
- `/aquariums/{id}/state-history` - Get complete state history
- `/aquariums/{id}/current-state-duration` - Get current state duration

#### Backward Compatibility
- All existing functionality is preserved
- Response formats remain consistent
- Authentication requirements unchanged
- Data models are enhanced but backward compatible

## Rate Limiting

The API implements rate limiting to ensure fair usage:
- **Authenticated requests:** 1000 requests per hour per user
- **Unauthenticated requests:** 100 requests per hour per IP
- **Bulk operations:** 50 requests per hour per user

Rate limit headers are included in responses:
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1234567890
```

## Support

For API support, please contact the development team or refer to the project documentation.

**API Version:** 1.0.0 beta  
**Last Updated:** January 2024  
**Documentation Version:** 2.0 