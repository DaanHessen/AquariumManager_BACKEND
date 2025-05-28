# Aquarium API Documentation

This document provides comprehensive documentation for the Aquarium API. The purpose of this documentation is to help frontend developers implement a user interface that interacts with the backend.

## Table of Contents
- [General API Information](#general-api-information)
- [Authentication](#authentication)
- [Aquariums](#aquariums)
- [Inhabitants](#inhabitants)
- [Accessories](#accessories)
- [Ornaments](#ornaments)
- [Error Handling](#error-handling)
- [Aquarium Parameters Simulation](#aquarium-parameters-simulation)
- [Health Check](#health-check)

## General API Information

- Base URL: `/api`
- All responses are wrapped in an `ApiResponse` object with the following structure:

```json
{
  "status": "success", // or "error"
  "data": {}, // response data or null in case of error
  "timestamp": 1620000000000, // Unix timestamp in milliseconds
  "message": "Operation successful" // or error message
}
```

## Authentication

### Register

Creates a new user account.

- **URL**: `/api/auth/register`
- **Method**: `POST`
- **Auth required**: No
- **Request Body**:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

- **Success Response**: 
  - **Code**: 201 Created
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "ownerId": 1,
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": 1620000000000,
  "message": "Registration successful"
}
```

### Login

Authenticates a user and returns a JWT token.

- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Auth required**: No
- **Request Body**:

```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "ownerId": 1,
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  },
  "timestamp": 1620000000000,
  "message": "Login successful"
}
```

## Aquariums

### Get All Aquariums

Returns a list of all aquariums.

- **URL**: `/api/aquariums`
- **Method**: `GET`
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Living Room Aquarium",
      "length": 100.0,
      "width": 40.0,
      "height": 50.0,
      "volumeInLiters": 200.0,
      "substrate": "GRAVEL",
      "waterType": "FRESH",
      "state": "RUNNING",
      "ownerId": 1,
      "ownerEmail": "john.doe@example.com",
      "inhabitants": [],
      "accessories": [],
      "ornaments": []
    }
  ],
  "timestamp": 1620000000000,
  "message": "Aquariums fetched successfully"
}
```

### Get Aquarium By ID

Returns a specific aquarium by ID.

- **URL**: `/api/aquariums/{id}`
- **Method**: `GET`
- **URL Parameters**: `id=[long]` where `id` is the ID of the aquarium
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "Living Room Aquarium",
    "length": 100.0,
    "width": 40.0,
    "height": 50.0,
    "volumeInLiters": 200.0,
    "substrate": "GRAVEL",
    "waterType": "FRESH",
    "state": "RUNNING",
    "ownerId": 1,
    "ownerEmail": "john.doe@example.com",
    "inhabitants": [],
    "accessories": [],
    "ornaments": []
  },
  "timestamp": 1620000000000,
  "message": "Aquarium fetched successfully"
}
```

### Get Aquarium Detail By ID

Returns detailed information about a specific aquarium.

- **URL**: `/api/aquariums/{id}/detail`
- **Method**: `GET`
- **URL Parameters**: `id=[long]` where `id` is the ID of the aquarium
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Similar to Get Aquarium By ID but with more details

### Create Aquarium

Creates a new aquarium.

- **URL**: `/api/aquariums`
- **Method**: `POST`
- **Auth required**: Yes (JWT token in Authorization header)
- **Request Body**:

```json
{
  "name": "Living Room Aquarium",
  "length": 100.0,
  "width": 40.0,
  "height": 50.0,
  "substrate": "GRAVEL",
  "waterType": "FRESH",
  "state": "SETUP"
}
```

- **Success Response**: 
  - **Code**: 201 Created
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "Living Room Aquarium",
    "length": 100.0,
    "width": 40.0,
    "height": 50.0,
    "volumeInLiters": 200.0,
    "substrate": "GRAVEL",
    "waterType": "FRESH",
    "state": "SETUP",
    "ownerId": 1,
    "ownerEmail": "john.doe@example.com",
    "inhabitants": [],
    "accessories": [],
    "ornaments": []
  },
  "timestamp": 1620000000000,
  "message": "Aquarium created successfully"
}
```

### Update Aquarium

Updates an existing aquarium.

- **URL**: `/api/aquariums/{id}`
- **Method**: `PUT`
- **URL Parameters**: `id=[long]` where `id` is the ID of the aquarium
- **Auth required**: Yes (JWT token in Authorization header)
- **Request Body**:

```json
{
  "name": "Updated Aquarium Name",
  "length": 120.0,
  "width": 45.0,
  "height": 55.0,
  "substrate": "SAND",
  "waterType": "FRESH",
  "state": "RUNNING"
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Similar to Create Aquarium response but with updated values

### Delete Aquarium

Deletes an aquarium.

- **URL**: `/api/aquariums/{id}`
- **Method**: `DELETE`
- **URL Parameters**: `id=[long]` where `id` is the ID of the aquarium
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "aquariumId": 1
  },
  "timestamp": 1620000000000,
  "message": "Aquarium deleted successfully"
}
```

### Add Accessory to Aquarium

Adds an accessory to an aquarium.

- **URL**: `/api/aquariums/{aquariumId}/accessories/{accessoryId}`
- **Method**: `POST`
- **URL Parameters**: 
  - `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
  - `accessoryId=[long]` where `accessoryId` is the ID of the accessory
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Request Body** (Optional properties for the accessory):

```json
{
  "position": "top",
  "isActive": true
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated aquarium with accessory added

### Remove Accessory from Aquarium

Removes an accessory from an aquarium.

- **URL**: `/api/aquariums/{aquariumId}/accessories/{accessoryId}`
- **Method**: `DELETE`
- **URL Parameters**: 
  - `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
  - `accessoryId=[long]` where `accessoryId` is the ID of the accessory
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated aquarium with accessory removed

### Add Ornament to Aquarium

Adds an ornament to an aquarium.

- **URL**: `/api/aquariums/{aquariumId}/ornaments/{ornamentId}`
- **Method**: `POST`
- **URL Parameters**: 
  - `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
  - `ornamentId=[long]` where `ornamentId` is the ID of the ornament
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Request Body** (Optional properties for the ornament):

```json
{
  "position": "center",
  "isVisible": true
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated aquarium with ornament added

### Remove Ornament from Aquarium

Removes an ornament from an aquarium.

- **URL**: `/api/aquariums/{aquariumId}/ornaments/{ornamentId}`
- **Method**: `DELETE`
- **URL Parameters**: 
  - `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
  - `ornamentId=[long]` where `ornamentId` is the ID of the ornament
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated aquarium with ornament removed

### Add Inhabitant to Aquarium

Adds an inhabitant to an aquarium.

- **URL**: `/api/aquariums/{aquariumId}/inhabitants/{inhabitantId}`
- **Method**: `POST`
- **URL Parameters**: 
  - `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
  - `inhabitantId=[long]` where `inhabitantId` is the ID of the inhabitant
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Request Body** (Optional properties for the inhabitant):

```json
{
  "position": "bottom",
  "health": "good"
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated aquarium with inhabitant added

### Remove Inhabitant from Aquarium

Removes an inhabitant from an aquarium.

- **URL**: `/api/aquariums/{aquariumId}/inhabitants/{inhabitantId}`
- **Method**: `DELETE`
- **URL Parameters**: 
  - `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
  - `inhabitantId=[long]` where `inhabitantId` is the ID of the inhabitant
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the aquarium)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated aquarium with inhabitant removed

## Inhabitants

### Get All Inhabitants

Returns a list of all inhabitants.

- **URL**: `/api/inhabitants`
- **Method**: `GET`
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "species": "Guppy",
      "color": "Rainbow",
      "count": 5,
      "isSchooling": true,
      "waterType": "FRESH",
      "aquariumId": 1,
      "type": "FISH",
      "isAggressiveEater": false,
      "requiresSpecialFood": false,
      "isSnailEater": false
    }
  ],
  "timestamp": 1620000000000,
  "message": "Inhabitants fetched successfully"
}
```

### Get Inhabitant By ID

Returns a specific inhabitant by ID.

- **URL**: `/api/inhabitants/{id}`
- **Method**: `GET`
- **URL Parameters**: `id=[long]` where `id` is the ID of the inhabitant
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "species": "Guppy",
    "color": "Rainbow",
    "count": 5,
    "isSchooling": true,
    "waterType": "FRESH",
    "aquariumId": 1,
    "type": "FISH",
    "isAggressiveEater": false,
    "requiresSpecialFood": false,
    "isSnailEater": false
  },
  "timestamp": 1620000000000,
  "message": "Inhabitant fetched successfully"
}
```

### Get Inhabitants By Aquarium

Returns all inhabitants in a specific aquarium.

- **URL**: `/api/inhabitants/byAquarium/{aquariumId}`
- **Method**: `GET`
- **URL Parameters**: `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: List of inhabitants in the specified aquarium

### Create Inhabitant

Creates a new inhabitant.

- **URL**: `/api/inhabitants`
- **Method**: `POST`
- **Auth required**: Yes (JWT token in Authorization header)
- **Request Body**:

```json
{
  "species": "Guppy",
  "color": "Rainbow",
  "count": 5,
  "isSchooling": true,
  "waterType": "FRESH",
  "type": "FISH",
  "aquariumId": 1,
  "isAggressiveEater": false,
  "requiresSpecialFood": false,
  "isSnailEater": false
}
```

- **Success Response**: 
  - **Code**: 201 Created
  - **Content**: Created inhabitant object

### Update Inhabitant

Updates an existing inhabitant.

- **URL**: `/api/inhabitants/{id}`
- **Method**: `PUT`
- **URL Parameters**: `id=[long]` where `id` is the ID of the inhabitant
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the inhabitant)
- **Request Body**:

```json
{
  "species": "Updated Species",
  "color": "Updated Color",
  "count": 10,
  "isSchooling": false,
  "waterType": "FRESH",
  "type": "FISH",
  "aquariumId": 1,
  "isAggressiveEater": true,
  "requiresSpecialFood": true,
  "isSnailEater": true
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated inhabitant object

### Delete Inhabitant

Deletes an inhabitant.

- **URL**: `/api/inhabitants/{id}`
- **Method**: `DELETE`
- **URL Parameters**: `id=[long]` where `id` is the ID of the inhabitant
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the inhabitant)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "inhabitantId": 1
  },
  "timestamp": 1620000000000,
  "message": "Inhabitant deleted successfully"
}
```

## Accessories

### Get All Accessories

Returns a list of all accessories.

- **URL**: `/api/accessories`
- **Method**: `GET`
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "model": "Filter Model X",
      "serialNumber": "FMX123",
      "type": "FILTER",
      "isExternal": true,
      "capacityLiters": 100,
      "isLed": null,
      "turnOnTime": null,
      "turnOffTime": null,
      "minTemperature": null,
      "maxTemperature": null,
      "currentTemperature": null
    }
  ],
  "timestamp": 1620000000000,
  "message": "Accessories fetched successfully"
}
```

### Get Accessory By ID

Returns a specific accessory by ID.

- **URL**: `/api/accessories/{id}`
- **Method**: `GET`
- **URL Parameters**: `id=[long]` where `id` is the ID of the accessory
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "model": "Filter Model X",
    "serialNumber": "FMX123",
    "type": "FILTER",
    "isExternal": true,
    "capacityLiters": 100,
    "isLed": null,
    "turnOnTime": null,
    "turnOffTime": null,
    "minTemperature": null,
    "maxTemperature": null,
    "currentTemperature": null
  },
  "timestamp": 1620000000000,
  "message": "Accessory fetched successfully"
}
```

### Get Accessories By Aquarium

Returns all accessories in a specific aquarium.

- **URL**: `/api/accessories/byAquarium/{aquariumId}`
- **Method**: `GET`
- **URL Parameters**: `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: List of accessories in the specified aquarium

### Create Accessory

Creates a new accessory.

- **URL**: `/api/accessories`
- **Method**: `POST`
- **Auth required**: Yes (JWT token in Authorization header)
- **Request Body**:

```json
{
  "model": "Filter Model X",
  "serialNumber": "FMX123",
  "type": "FILTER",
  "aquariumId": 1,
  "isExternal": true,
  "capacityInLiters": 100,
  "isLED": null,
  "color": null,
  "timeOn": null,
  "timeOff": null,
  "minTemperature": null,
  "maxTemperature": null,
  "currentTemperature": null
}
```

For a light accessory:

```json
{
  "model": "Light Model Y",
  "serialNumber": "LMY456",
  "type": "LIGHT",
  "aquariumId": 1,
  "isExternal": null,
  "capacityInLiters": null,
  "isLED": true,
  "color": "white",
  "timeOn": "08:00:00",
  "timeOff": "20:00:00",
  "minTemperature": null,
  "maxTemperature": null,
  "currentTemperature": null
}
```

For a heater accessory:

```json
{
  "model": "Heater Model Z",
  "serialNumber": "HMZ789",
  "type": "HEATER",
  "aquariumId": 1,
  "isExternal": null,
  "capacityInLiters": null,
  "isLED": null,
  "color": null,
  "timeOn": null,
  "timeOff": null,
  "minTemperature": 22.0,
  "maxTemperature": 28.0,
  "currentTemperature": 25.0
}
```

- **Success Response**: 
  - **Code**: 201 Created
  - **Content**: Created accessory object

### Update Accessory

Updates an existing accessory.

- **URL**: `/api/accessories/{id}`
- **Method**: `PUT`
- **URL Parameters**: `id=[long]` where `id` is the ID of the accessory
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the accessory)
- **Request Body**: Same format as Create Accessory with updated values
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated accessory object

### Delete Accessory

Deletes an accessory.

- **URL**: `/api/accessories/{id}`
- **Method**: `DELETE`
- **URL Parameters**: `id=[long]` where `id` is the ID of the accessory
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the accessory)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "accessoryId": 1
  },
  "timestamp": 1620000000000,
  "message": "Accessory deleted successfully"
}
```

## Ornaments

### Get All Ornaments

Returns a list of all ornaments.

- **URL**: `/api/ornaments`
- **Method**: `GET`
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Castle",
      "description": "A decorative castle",
      "color": "Gray",
      "isAirPumpCompatible": true
    }
  ],
  "timestamp": 1620000000000,
  "message": "Ornaments fetched successfully"
}
```

### Get Ornament By ID

Returns a specific ornament by ID.

- **URL**: `/api/ornaments/{id}`
- **Method**: `GET`
- **URL Parameters**: `id=[long]` where `id` is the ID of the ornament
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "Castle",
    "description": "A decorative castle",
    "color": "Gray",
    "isAirPumpCompatible": true
  },
  "timestamp": 1620000000000,
  "message": "Ornament fetched successfully"
}
```

### Get Ornaments By Aquarium

Returns all ornaments in a specific aquarium.

- **URL**: `/api/ornaments/byAquarium/{aquariumId}`
- **Method**: `GET`
- **URL Parameters**: `aquariumId=[long]` where `aquariumId` is the ID of the aquarium
- **Auth required**: No
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: List of ornaments in the specified aquarium

### Create Ornament

Creates a new ornament.

- **URL**: `/api/ornaments`
- **Method**: `POST`
- **Auth required**: Yes (JWT token in Authorization header)
- **Request Body**:

```json
{
  "name": "Castle",
  "description": "A decorative castle",
  "color": "Gray",
  "supportsAirPump": true,
  "aquariumId": 1
}
```

- **Success Response**: 
  - **Code**: 201 Created
  - **Content**: Created ornament object

### Update Ornament

Updates an existing ornament.

- **URL**: `/api/ornaments/{id}`
- **Method**: `PUT`
- **URL Parameters**: `id=[long]` where `id` is the ID of the ornament
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the ornament)
- **Request Body**:

```json
{
  "name": "Updated Castle",
  "description": "Updated description",
  "color": "Updated color",
  "supportsAirPump": false,
  "aquariumId": 1
}
```

- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: Updated ornament object

### Delete Ornament

Deletes an ornament.

- **URL**: `/api/ornaments/{id}`
- **Method**: `DELETE`
- **URL Parameters**: `id=[long]` where `id` is the ID of the ornament
- **Auth required**: Yes (JWT token in Authorization header, must be owner of the ornament)
- **Success Response**: 
  - **Code**: 200 OK
  - **Content**: 

```json
{
  "status": "success",
  "data": {
    "ornamentId": 1
  },
  "timestamp": 1620000000000,
  "message": "Ornament deleted successfully"
}
```

## Error Handling

All endpoints can return the following error responses:

### 400 Bad Request

When the request is invalid.

```json
{
  "status": "error",
  "data": null,
  "timestamp": 1620000000000,
  "message": "Invalid request: [specific error message]"
}
```

### 401 Unauthorized

When authentication is required but not provided or is invalid.

```json
{
  "status": "error",
  "data": null,
  "timestamp": 1620000000000,
  "message": "Authentication required"
}
```

### 403 Forbidden

When the authenticated user doesn't have permission to access the resource.

```json
{
  "status": "error",
  "data": null,
  "timestamp": 1620000000000,
  "message": "You don't have permission to access this resource"
}
```

### 404 Not Found

When the requested resource is not found.

```json
{
  "status": "error",
  "data": null,
  "timestamp": 1620000000000,
  "message": "Resource not found"
}
```

### 500 Internal Server Error

When an unexpected error occurs on the server.

```json
{
  "status": "error",
  "data": null,
  "timestamp": 1620000000000,
  "message": "An unexpected error occurred"
}
```

## Enums

### AquariumState
- `SETUP`
- `RUNNING`
- `MAINTENANCE`
- `INACTIVE`

### SubstrateType
- `SAND`
- `GRAVEL`
- `SOIL`

### WaterType
- `FRESH`
- `SALT`

## Aquarium Parameters Simulation

The backend provides simulated water parameters for aquariums, which are useful for monitoring water quality without requiring manual input.

### Get Aquarium Parameters

Retrieves real-time simulated parameters for an aquarium.

**URL**: `/api/aquariums/{id}/parameters`  
**Method**: `GET`  
**Auth required**: Yes

**URL Parameters**:
- `id` - The ID of the aquarium

**Success Response**:
- **Code**: 200 OK
- **Content**:
```json
{
  "status": "success",
  "data": {
    "temperature": 25.6,
    "pH": 7.2,
    "ammonia": 0.02,
    "nitrite": 0.0,
    "nitrate": 7.5,
    "hardness": 6.8,
    "kH": 4.9
  }
}
```

**Error Response**:
- **Code**: 404 Not Found
- **Content**:
```json
{
  "status": "error",
  "message": "Aquarium not found"
}
```

OR

- **Code**: 200 OK
- **Content**:
```json
{
  "status": "error",
  "message": "Parameters are only available when the aquarium is in RUNNING state"
}
```

### Reset Aquarium Parameters

Resets the simulated parameters for an aquarium, e.g., after a water change.

**URL**: `/api/aquariums/{id}/parameters/reset`  
**Method**: `POST`  
**Auth required**: Yes

**URL Parameters**:
- `id` - The ID of the aquarium

**Success Response**:
- **Code**: 200 OK
- **Content**:
```json
{
  "status": "success",
  "message": "Parameters reset successfully"
}
```

**Error Response**:
- **Code**: 404 Not Found
- **Content**:
```json
{
  "status": "error",
  "message": "Aquarium not found"
}
```

### Note on Aquarium Detail Endpoint

The `/api/aquariums/{id}` endpoint now includes simulated parameters when the aquarium is in RUNNING state.

## Authentication

All secured endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

The token is obtained via the login or register endpoints.

## Health Check
- `GET /api/health` - Application health status (includes database connectivity)
- `GET /api/health/basic` - Basic service health status (no database dependency) 