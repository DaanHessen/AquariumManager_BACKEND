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
}