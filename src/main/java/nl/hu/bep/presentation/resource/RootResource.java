package nl.hu.bep.presentation.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.config.AquariumConstants;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.presentation.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class RootResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        
        // API versioning
        apiInfo.put("version", "v2.0.0");
        apiInfo.put("name", "Aquarium Management API");
        
        // Description
        apiInfo.put("description", "RESTful API for managing aquariums, inhabitants, accessories, and ornaments");
        
        // Available endpoints
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("aquariums", AquariumConstants.API_BASE_PATH + AquariumConstants.AQUARIUMS_PATH);
        endpoints.put("inhabitants", AquariumConstants.API_BASE_PATH + AquariumConstants.INHABITANTS_PATH);
        endpoints.put("accessories", AquariumConstants.API_BASE_PATH + AquariumConstants.ACCESSORIES_PATH);
        endpoints.put("ornaments", AquariumConstants.API_BASE_PATH + AquariumConstants.ORNAMENTS_PATH);
        endpoints.put("authentication", AquariumConstants.API_BASE_PATH + AquariumConstants.AUTH_BASE_PATH);
        apiInfo.put("endpoints", endpoints);
        
        // Database health status
        Map<String, Object> databaseHealth = getDatabaseHealth();
        apiInfo.put("database", databaseHealth);
        
        // Additional metadata
        apiInfo.put("timestamp", LocalDateTime.now());
        apiInfo.put("server_status", "operational");
        
        return Response.ok(ApiResponse.success(apiInfo, "API information retrieved successfully")).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getApiDocumentation() {
        String html = generateApiDocumentation();
        return Response.ok(html).build();
    }
    
    private Map<String, Object> getDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try {
            boolean isHealthy = DatabaseConfig.isHealthy();
            dbHealth.put("status", isHealthy ? "UP" : "DOWN");
            dbHealth.put("message", isHealthy ? "Database connection successful" : "Database connection failed");
            dbHealth.put("checked_at", LocalDateTime.now());
        } catch (Exception e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("message", "Database health check failed: " + e.getMessage());
            dbHealth.put("checked_at", LocalDateTime.now());
        }
        
        return dbHealth;
    }
    
    private String generateApiDocumentation() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Aquarium Manager API Documentation</title>
                <style>
                    body { font-family: Arial, sans-serif; max-width: 1200px; margin: 0 auto; padding: 20px; line-height: 1.6; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
                    .endpoint { background: #f8f9fa; border-left: 4px solid #007bff; padding: 20px; margin: 20px 0; border-radius: 5px; }
                    .method { display: inline-block; padding: 4px 12px; border-radius: 4px; font-weight: bold; color: white; margin-right: 10px; }
                    .GET { background-color: #28a745; }
                    .POST { background-color: #007bff; }
                    .PUT { background-color: #ffc107; color: #212529; }
                    .DELETE { background-color: #dc3545; }
                    .code { background: #e9ecef; padding: 10px; border-radius: 4px; font-family: 'Courier New', monospace; margin: 10px 0; }
                    .auth-required { color: #856404; background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 8px; border-radius: 4px; margin: 10px 0; }
                    h2 { color: #343a40; border-bottom: 2px solid #007bff; padding-bottom: 10px; }
                    h3 { color: #495057; }
                    .base-url { background: #d1ecf1; border: 1px solid #bee5eb; padding: 10px; border-radius: 4px; margin: 20px 0; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>🐠 Aquarium Manager API</h1>
                    <p>RESTful API for managing aquariums, inhabitants, accessories, and ornaments</p>
                    <p><strong>Version:</strong> v2.0.0</p>
                </div>
                
                <div class="base-url">
                    <strong>Base URL:</strong> <code>https://web-production-8a8d.up.railway.app/api</code>
                </div>
                
                <h2>🔐 Authentication</h2>
                <p>Most endpoints require JWT authentication. Include the token in the Authorization header:</p>
                <div class="code">Authorization: Bearer YOUR_JWT_TOKEN</div>
                
                <div class="endpoint">
                    <h3><span class="method POST">POST</span>/auth/register</h3>
                    <p>Register a new user account</p>
                    <div class="code">
{
  "firstName": "John",
  "lastName": "Doe", 
  "email": "john@example.com",
  "password": "password123"
}
                    </div>
                    <p><strong>Response:</strong> Returns JWT token and owner ID</p>
                </div>
                
                <div class="endpoint">
                    <h3><span class="method POST">POST</span>/auth/login</h3>
                    <p>Login with existing credentials</p>
                    <div class="code">
{
  "email": "john@example.com",
  "password": "password123"
}
                    </div>
                </div>
                
                <h2>🏠 Aquariums</h2>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/aquariums</h3>
                    <p>Get all aquariums for the authenticated user</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method POST">POST</span>/aquariums</h3>
                    <p>Create a new aquarium</p>
                    <div class="code">
{
  "name": "My Tropical Tank",
  "description": "Beautiful tropical aquarium",
  "length": 100.0,
  "width": 40.0,
  "height": 50.0,
  "waterType": "FRESHWATER",
  "hasLighting": true,
  "hasHeater": true,
  "hasFilter": true
}
                    </div>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/aquariums/{id}</h3>
                    <p>Get details of a specific aquarium</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method PUT">PUT</span>/aquariums/{id}</h3>
                    <p>Update an existing aquarium</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method DELETE">DELETE</span>/aquariums/{id}</h3>
                    <p>Delete an aquarium</p>
                </div>
                
                <h2>🐟 Inhabitants</h2>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/inhabitants</h3>
                    <p>Get all inhabitants for the authenticated user</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method POST">POST</span>/inhabitants</h3>
                    <p>Create a new inhabitant (fish, plant, snail, etc.)</p>
                    <div class="code">
{
  "species": "Goldfish",
  "color": "Orange",
  "description": "Beautiful goldfish",
  "count": 1,
  "isSchooling": false,
  "waterType": "FRESHWATER",
  "type": "FISH",
  "aquariumId": 1,
  "isAggressiveEater": false,
  "requiresSpecialFood": false,
  "isSnailEater": false,
  "name": "Goldie",
  "age": 2,
  "gender": "FEMALE"
}
                    </div>
                    <p><strong>Types:</strong> FISH, PLANT, SNAIL, SHRIMP, CRAYFISH, CORAL</p>
                    <p><strong>Water Types:</strong> FRESHWATER, SALTWATER, BRACKISH</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/inhabitants/{id}</h3>
                    <p>Get details of a specific inhabitant</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method PUT">PUT</span>/inhabitants/{id}</h3>
                    <p>Update an existing inhabitant</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method DELETE">DELETE</span>/inhabitants/{id}</h3>
                    <p>Delete an inhabitant</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/inhabitants/aquarium/{aquariumId}</h3>
                    <p>Get all inhabitants in a specific aquarium</p>
                </div>
                
                <h2>🛠️ Accessories</h2>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/accessories</h3>
                    <p>Get all accessories for the authenticated user</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method POST">POST</span>/accessories</h3>
                    <p>Create a new accessory (filter, heater, lighting, etc.)</p>
                    <div class="code">
{
  "name": "Canister Filter",
  "type": "FILTER",
  "description": "High-quality canister filter",
  "aquariumId": 1,
  "isWorking": true,
  "lastMaintenance": "2025-06-01"
}
                    </div>
                    <p><strong>Types:</strong> FILTER, HEATER, LIGHTING, PUMP, SKIMMER, UV_STERILIZER</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/accessories/{id}</h3>
                    <p>Get details of a specific accessory</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method PUT">PUT</span>/accessories/{id}</h3>
                    <p>Update an existing accessory</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method DELETE">DELETE</span>/accessories/{id}</h3>
                    <p>Delete an accessory</p>
                </div>
                
                <h2>🎨 Ornaments</h2>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/ornaments</h3>
                    <p>Get all ornaments for the authenticated user</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method POST">POST</span>/ornaments</h3>
                    <p>Create a new ornament (decoration, substrate, rock, etc.)</p>
                    <div class="code">
{
  "name": "Castle Decoration",
  "type": "DECORATION",
  "description": "Medieval castle ornament",
  "material": "CERAMIC",
  "size": "MEDIUM",
  "aquariumId": 1
}
                    </div>
                    <p><strong>Types:</strong> DECORATION, SUBSTRATE, ROCK, DRIFTWOOD, ARTIFICIAL_PLANT</p>
                    <p><strong>Materials:</strong> CERAMIC, PLASTIC, NATURAL_STONE, WOOD, GLASS, METAL</p>
                    <p><strong>Sizes:</strong> SMALL, MEDIUM, LARGE, EXTRA_LARGE</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method GET">GET</span>/ornaments/{id}</h3>
                    <p>Get details of a specific ornament</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method PUT">PUT</span>/ornaments/{id}</h3>
                    <p>Update an existing ornament</p>
                </div>
                
                <div class="endpoint">
                    <div class="auth-required">🔒 Authentication Required</div>
                    <h3><span class="method DELETE">DELETE</span>/ornaments/{id}</h3>
                    <p>Delete an ornament</p>
                </div>
                
                <h2>📊 Response Format</h2>
                <p>All API responses follow a consistent format:</p>
                <div class="code">
{
  "status": "success|error",
  "data": { ... },
  "timestamp": 1750942763854,
  "message": "Human readable message"
}
                </div>
                
                <h2>❌ Error Codes</h2>
                <p><strong>400</strong> - Bad Request: Invalid input data</p>
                <p><strong>401</strong> - Unauthorized: Missing or invalid authentication token</p>
                <p><strong>403</strong> - Forbidden: User doesn't have permission to access resource</p>
                <p><strong>404</strong> - Not Found: Resource doesn't exist</p>
                <p><strong>409</strong> - Conflict: Resource already exists</p>
                <p><strong>500</strong> - Internal Server Error: Server-side error</p>
                
                <div style="margin-top: 50px; text-align: center; color: #6c757d;">
                    <p>Aquarium Manager API v2.0.0 | Built with ❤️ for aquarium enthusiasts</p>
                </div>
            </body>
            </html>
            """;
    }
}