package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import nl.hu.bep.application.service.AquariumService;
import nl.hu.bep.presentation.dto.request.AquariumRequest;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.presentation.dto.response.AquariumResponse;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.SecurityContextHelper;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Slf4j
@Path("/aquariums")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Secured
public class AquariumResource {

    private final AquariumService aquariumService;

    @Inject
    public AquariumResource(AquariumService aquariumService) {
        this.aquariumService = aquariumService;
    }

    @GET
    public Response getAllAquariums(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<AquariumResponse> aquariums = aquariumService.getAllAquariums(ownerId);
        return Response.ok(ApiResponse.success(aquariums, "Aquariums retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "id")
    public Response getAquarium(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse aquarium = aquariumService.getAquarium(id, ownerId);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium retrieved successfully")).build();
    }

    @POST
    public Response createAquarium(AquariumRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse aquarium = aquariumService.createAquarium(request, ownerId);
        
        Map<String, Object> responseData = Map.of(
            "aquarium", aquarium,
            "id", aquarium.id()
        );
        
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(responseData, "Aquarium created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "id")
    public Response updateAquarium(@PathParam("id") Long id, AquariumRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse aquarium = aquariumService.updateAquarium(id, request, ownerId);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "id")
    public Response deleteAquarium(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        aquariumService.deleteAquarium(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Aquarium deleted successfully")).build();
    }
}