package nl.hu.bep.security.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.presentation.dto.security.AuthRequest;
import nl.hu.bep.security.model.AuthResponse;
import nl.hu.bep.security.model.RegisterRequest;
import nl.hu.bep.security.application.service.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final AuthenticationService authService;

    @Inject
    public AuthResource(AuthenticationService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        AuthResponse response = authService.register(request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("ownerId", response.ownerId());
        responseData.put("token", response.token());

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(responseData, "Registration successful"))
                .build();
    }

    @POST
    @Path("/login")
    public Response login(AuthRequest request) {
        AuthResponse response = authService.authenticate(request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("ownerId", response.ownerId());
        responseData.put("token", response.token());

        return Response.ok(ApiResponse.success(responseData, "Login successful"))
                .build();
    }
}