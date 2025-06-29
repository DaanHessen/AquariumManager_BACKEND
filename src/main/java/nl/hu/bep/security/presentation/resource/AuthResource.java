package nl.hu.bep.security.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.hu.bep.presentation.dto.request.AuthRequest;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.security.application.service.AuthenticationService;
import nl.hu.bep.security.model.request.RegisterRequest;
import nl.hu.bep.security.model.response.AuthResponse;

import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final AuthenticationService authenticationService;

    @Inject
    public AuthResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Path("/register")
    public Response register(RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);

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
        AuthResponse response = authenticationService.authenticate(request);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("ownerId", response.ownerId());
        responseData.put("token", response.token());

        return Response.ok(ApiResponse.success(responseData, "Login successful"))
                .build();
    }
}