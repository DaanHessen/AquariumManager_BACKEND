package nl.hu.bep.presentation.resource;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.config.DatabaseConfig;
import nl.hu.bep.presentation.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@Path("/")
@Singleton
public class RootResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("aquariums", "/api/aquariums");
        endpoints.put("inhabitants", "/api/inhabitants");
        endpoints.put("authentication", "/api/auth");
        endpoints.put("status-detailed", "/api/status");
        endpoints.put("health-basic", "/health");

        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("name", "Aquarium API");
        apiInfo.put("version", "2.0.0");
        apiInfo.put("endpoints", endpoints);
        apiInfo.put("notes", Map.of(
            "health-basic", "Simple health check for Railway deployment",
            "status-detailed", "Detailed health check including database connectivity"
        ));

        return Response.ok(ApiResponse.success(apiInfo)).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response detailedHealthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "Aquarium API - Detailed Health Check");
        
        // Add environment info for debugging
        health.put("environment", getEnvironmentInfo());
        
        // Check database health
        boolean dbHealthy = false;
        String dbError = null;
        
        try {
            dbHealthy = DatabaseConfig.isHealthy();
        } catch (Exception e) {
            dbError = e.getMessage();
        }
        
        health.put("database", dbHealthy ? "UP" : "DOWN");
        if (dbError != null) {
            health.put("database_error", dbError);
        }
        
        if (dbHealthy) {
            return Response.ok(ApiResponse.success(health, "Service is healthy")).build();
        } else {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(ApiResponse.error(health, "Service is unhealthy - database connection failed"))
                    .build();
        }
    }
    
    private Map<String, Object> getEnvironmentInfo() {
        Map<String, Object> env = new HashMap<>();
        
        // Check for Railway's DATABASE_URL
        String databaseUrl = System.getenv("DATABASE_URL");
        env.put("DATABASE_URL_present", databaseUrl != null && !databaseUrl.isEmpty());
        
        // Check for individual DB environment variables
        env.put("DB_HOST", System.getenv("DB_HOST") != null ? "present" : "missing");
        env.put("DB_PORT", System.getenv("DB_PORT") != null ? "present" : "missing");
        env.put("DB_NAME", System.getenv("DB_NAME") != null ? "present" : "missing");
        env.put("DB_USER", System.getenv("DB_USER") != null ? "present" : "missing");
        env.put("DB_PASSWORD", System.getenv("DB_PASSWORD") != null ? "present" : "missing");
        
        // Add PORT info for Railway
        env.put("PORT", System.getenv("PORT"));
        
        return env;
    }
}