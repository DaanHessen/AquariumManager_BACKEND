package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.config.AquariumConstants;
import nl.hu.bep.config.DatabaseManager;
import nl.hu.bep.presentation.dto.response.ApiResponse;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class RootResource {

    private final DatabaseManager databaseManager;

    @Inject
    public RootResource(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        
        apiInfo.put("version", "v2.1.1");
        apiInfo.put("name", "Aquarium Management API");
        
        apiInfo.put("description", "RESTful API for managing aquariums, inhabitants, accessories, and ornaments");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("aquariums", AquariumConstants.API_BASE_PATH + AquariumConstants.AQUARIUMS_PATH);
        endpoints.put("inhabitants", AquariumConstants.API_BASE_PATH + AquariumConstants.INHABITANTS_PATH);
        endpoints.put("accessories", AquariumConstants.API_BASE_PATH + AquariumConstants.ACCESSORIES_PATH);
        endpoints.put("ornaments", AquariumConstants.API_BASE_PATH + AquariumConstants.ORNAMENTS_PATH);
        endpoints.put("authentication", AquariumConstants.API_BASE_PATH + AquariumConstants.AUTH_BASE_PATH);
        apiInfo.put("endpoints", endpoints);
        
        Map<String, Object> databaseHealth = getDatabaseHealth();
        apiInfo.put("database", databaseHealth);
        
        apiInfo.put("timestamp", LocalDateTime.now());
        apiInfo.put("server_status", "operational");
        
        return Response.ok(ApiResponse.success(apiInfo, "API information retrieved successfully")).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getApiDocumentation() {
        try {
            InputStream htmlStream = getClass().getClassLoader().getResourceAsStream("index.html");
            if (htmlStream == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("<h1>API Documentation Not Found</h1><p>The documentation file could not be loaded.</p>")
                    .build();
            }
            
            String html = new String(htmlStream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
            return Response.ok(html).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("<h1>Error Loading Documentation</h1><p>Failed to load API documentation: " + e.getMessage() + "</p>")
                .build();
        }
    }
    
    private Map<String, Object> getDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try (Connection connection = databaseManager.getConnection()) {
            boolean isHealthy = connection != null && !connection.isClosed();
            dbHealth.put("status", isHealthy ? "UP" : "DOWN");
            dbHealth.put("message", isHealthy ? "Database connection successful" : "Database connection failed");
        } catch (SQLException e) {
            dbHealth.put("status", "DOWN");
            dbHealth.put("message", "Database health check failed: " + e.getMessage());
        }
        
        dbHealth.put("checked_at", LocalDateTime.now());
        return dbHealth;
    }
}