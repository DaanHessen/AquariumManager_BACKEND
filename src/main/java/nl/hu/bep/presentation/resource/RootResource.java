package nl.hu.bep.presentation.resource;

import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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

        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("name", "Aquarium API");
        apiInfo.put("version", "1.0.0 beta");
        apiInfo.put("endpoints", endpoints);

        return Response.ok(ApiResponse.success(apiInfo)).build();
    }
}