# Aquarium Manager API Documentation v2.0

**Base URL:** `https://your-domain.com/api`

## Quick Start

1. **Register/Login** → Get JWT token  
2. **Include token** in `Authorization: Bearer <token>` header for authenticated endpoints  
3. **All responses** use `ApiResponse<T>` wrapper with `{status, data, timestamp, message}`

## Core Data Models

### Enums

```typescript
enum AquariumState { SETUP, RUNNING, MAINTENANCE, INACTIVE }
enum SubstrateType { SAND, GRAVEL, SOIL }
enum WaterType { FRESH, SALT }
```

### Main Models

```typescript
interface Aquarium {
  id: number, name: string, length: number, width: number, height: number,
  volumeInLiters: number, substrate: SubstrateType, waterType: WaterType,
  state: AquariumState, currentStateStartTime: string, currentStateDurationMinutes: number,
  ownerId: number, ownerEmail: string, color?: string, description?: string,
  dateCreated: string, inhabitants: Inhabitant[], accessories: Accessory[], ornaments: Ornament[]
}

interface Inhabitant {
  id: number, species: string, color?: string, description?: string,
  dateCreated: string, count: number, isSchooling: boolean, waterType: WaterType,
  aquariumId?: number, type: string, isAggressiveEater: boolean,
  requiresSpecialFood: boolean, isSnailEater: boolean
}

interface Accessory {
  id: number, model: string, serialNumber: string, type: string,
  isExternal?: boolean, capacityLiters?: number, isLed?: boolean,
  turnOnTime?: string, turnOffTime?: string, minTemperature?: number,
  maxTemperature?: number, currentTemperature?: number, color?: string,
  description?: string, dateCreated: string
}

interface Ornament {
  id: number, name: string, color?: string, material: string,
  description?: string, dateCreated: string, isAirPumpCompatible: boolean
}

interface StateHistory {
  id: number, state: AquariumState, startTime: string, endTime?: string,
  durationMinutes?: number, isActive: boolean, createdAt: string
}
```

## Authentication Endpoints

### POST `/auth/register`

**Body:** `{firstName: string, lastName: string, email: string, password: string}`  
**Response:** `{ownerId: number, token: string}`

### POST `/auth/login`  

**Body:** `{email: string, password: string}`  
**Response:** `{ownerId: number, token: string}`

## Health & Info Endpoints

### GET `/` - API Info

**Auth:** None  
**Response:** API metadata and available endpoints

### GET `/status` - Detailed Health Check

**Auth:** None  
**Response:** Service status, database connectivity, environment info

## Aquarium Management `/aquariums`

### GET `/aquariums` 🔐

**Auth:** Required  
**Response:** `Aquarium[]` (all user's aquariums)

### GET `/aquariums/{id}`

**Auth:** None  
**Response:** `Aquarium` (basic info)

### GET `/aquariums/{id}/detail`  

**Auth:** None  
**Response:** `Aquarium` (with all collections)

### POST `/aquariums` 🔐

**Auth:** Required  
**Body:** `{name: string, length: number, width: number, height: number, substrate: SubstrateType, waterType: WaterType, color?: string, description?: string, state?: AquariumState}`  
**Response:** `Aquarium` (created aquarium)

### PUT `/aquariums/{id}` 🔐

**Auth:** Required + Ownership  
**Body:** Same as POST  
**Response:** `Aquarium` (updated)

### DELETE `/aquariums/{id}` 🔐  

**Auth:** Required + Ownership  
**Response:** `{aquariumId: number}`

## State History `/aquariums/{id}`

### GET `/aquariums/{id}/state-history`

**Auth:** None  
**Response:** `StateHistory[]` (complete history)

### GET `/aquariums/{id}/current-state-duration`  

**Auth:** None  
**Response:** `{aquariumId: number, currentStateDurationMinutes: number}`

## Accessories `/aquariums`

### GET `/aquariums/accessories` 🔐

**Auth:** Required  
**Response:** `Accessory[]` (all user's accessories)

### GET `/aquariums/accessories/{id}`

**Auth:** None  
**Response:** `Accessory`

### GET `/aquariums/{aquariumId}/accessories`

**Auth:** None  
**Response:** `Accessory[]` (aquarium's accessories)

### DELETE `/aquariums/accessories/{id}` 🔐

**Auth:** Required + Ownership  
**Response:** `{accessoryId: number}`

### POST `/aquariums/{aquariumId}/accessories/{accessoryId}` 🔐

**Auth:** Required + Ownership  
**Body:** `{[key: string]: any}` (properties)  
**Response:** `Aquarium` (updated with accessory)

### DELETE `/aquariums/{aquariumId}/accessories/{accessoryId}` 🔐  

**Auth:** Required + Ownership  
**Response:** `Aquarium` (updated)

## Ornaments `/aquariums`

### GET `/aquariums/ornaments` 🔐

**Auth:** Required  
**Response:** `Ornament[]` (all user's ornaments)

### GET `/aquariums/ornaments/{id}`

**Auth:** None  
**Response:** `Ornament`

### GET `/aquariums/{aquariumId}/ornaments`

**Auth:** None  
**Response:** `Ornament[]` (aquarium's ornaments)

### POST `/aquariums/{aquariumId}/ornaments/{ornamentId}` 🔐

**Auth:** Required + Ownership  
**Body:** `{[key: string]: any}` (properties)  
**Response:** `Aquarium` (updated with ornament)

### DELETE `/aquariums/{aquariumId}/ornaments/{ornamentId}` 🔐

**Auth:** Required + Ownership  
**Response:** `Aquarium` (updated)

## Inhabitants `/aquariums`

### GET `/aquariums/inhabitants` 🔐

**Auth:** Required  
**Response:** `Inhabitant[]` (all user's inhabitants)

### GET `/aquariums/inhabitants/{id}`

**Auth:** None  
**Response:** `Inhabitant`

### GET `/aquariums/{aquariumId}/inhabitants`

**Auth:** None  
**Response:** `Inhabitant[]` (aquarium's inhabitants)

## Standalone Resources

### Inhabitants `/inhabitants`

#### GET `/inhabitants` 🔐

**Auth:** Required  
**Response:** `Inhabitant[]`

#### GET `/inhabitants/{id}`

**Auth:** None  
**Response:** `Inhabitant`

#### GET `/inhabitants/byAquarium/{aquariumId}`

**Auth:** None  
**Response:** `Inhabitant[]`

#### POST `/inhabitants` 🔐

**Auth:** Required  
**Body:** `{species: string, color?: string, description?: string, count: number, isSchooling?: boolean, waterType: WaterType, type: string, aquariumId?: number, isAggressiveEater?: boolean, requiresSpecialFood?: boolean, isSnailEater?: boolean, name?: string}`  
**Response:** `Inhabitant`

#### PUT `/inhabitants/{id}` 🔐

**Auth:** Required + Ownership  
**Body:** Same as POST  
**Response:** `Inhabitant`

#### DELETE `/inhabitants/{id}` 🔐

**Auth:** Required + Ownership  
**Response:** `{inhabitantId: number}`

### Accessories `/accessories`

#### GET `/accessories` 🔐

**Auth:** Required  
**Response:** `Accessory[]`

#### GET `/accessories/{id}`

**Auth:** None  
**Response:** `Accessory`

#### GET `/accessories/byAquarium/{aquariumId}`
**Auth:** None  
**Response:** `Accessory[]`

#### POST `/accessories` 🔐

**Auth:** Required  
**Body:** `{model: string, serialNumber: string, type: string, aquariumId?: number, isExternal?: boolean, capacityLiters?: number, isLED?: boolean, color?: string, description?: string, timeOn?: string, timeOff?: string, minTemperature?: number, maxTemperature?: number, currentTemperature?: number}`  
**Response:** `Accessory`

#### PUT `/accessories/{id}` 🔐

**Auth:** Required + Ownership  
**Body:** Same as POST  
**Response:** `Accessory`

#### DELETE `/accessories/{id}` 🔐

**Auth:** Required + Ownership  
**Response:** `{accessoryId: number}`

### Ornaments `/ornaments`

#### GET `/ornaments` 🔐

**Auth:** Required  
**Response:** `Ornament[]`

#### GET `/ornaments/{id}`

**Auth:** None  
**Response:** `Ornament`

#### GET `/ornaments/byAquarium/{aquariumId}`

**Auth:** None  
**Response:** `Ornament[]`

#### POST `/ornaments` 🔐

**Auth:** Required  
**Body:** `{name: string, color?: string, material: string, description?: string, supportsAirPump?: boolean, aquariumId?: number}`  
**Response:** `Ornament`

#### PUT `/ornaments/{id}` 🔐

**Auth:** Required + Ownership  
**Body:** Same as POST  
**Response:** `Ornament`

#### DELETE `/ornaments/{id}` 🔐

**Auth:** Required + Ownership  
**Response:** `{ornamentId: number}`

## Error Responses

All errors follow this format:

```json
{
  "status": "error",
  "data": {
    "status": 400|401|403|404|500,
    "error": "ErrorType",
    "message": "Description",
    "path": "/api/path",
    "timestamp": 1234567890123,
    "details": {...}
  },
  "timestamp": 1234567890123,
  "message": "Error description"
}
```

### Common Status Codes

- **400** Bad Request (validation errors)
- **401** Unauthorized (missing/invalid token)  
- **403** Forbidden (insufficient permissions)
- **404** Not Found (resource doesn't exist)
- **409** Conflict (resource already exists)
- **500** Internal Server Error

## Development Notes

### CORS

- Configured for Vercel frontend (`*.vercel.app`)
- Localhost allowed for development
- Credentials supported

### Authentication Flow

1. Register/Login → Receive JWT token
2. Store token securely (localStorage/cookie)
3. Include in Authorization header: `Bearer <token>`
4. Token contains: `{sub: userId, username: email, ...}`

### Key Relationships

- **Aquarium** ← Owner (1:many)
- **Aquarium** ← Inhabitants/Accessories/Ornaments (1:many)
- **State History** tracks aquarium state changes
- **Water Type compatibility** enforced between aquarium ↔ inhabitants

### Usage Examples

**Create Aquarium:**

```javascript
POST /api/aquariums
{
  "name": "My Tank",
  "length": 100.0,
  "width": 40.0,  
  "height": 50.0,
  "substrate": "GRAVEL",
  "waterType": "FRESH",
  "state": "SETUP"
}
```

**Add Fish:**

```javascript
POST /api/inhabitants
{
  "species": "Neon Tetra",
  "count": 10,
  "isSchooling": true,
  "waterType": "FRESH",
  "type": "Fish",
  "aquariumId": 1
}
```

---
**Version:** 2.0.0 | **Updated:** 5/30/2025