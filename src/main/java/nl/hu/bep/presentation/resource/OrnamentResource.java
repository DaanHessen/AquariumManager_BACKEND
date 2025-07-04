package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import nl.hu.bep.application.service.OrnamentService;
import nl.hu.bep.presentation.dto.request.OrnamentRequest;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.presentation.dto.response.OrnamentResponse;
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
@Path("/ornaments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Secured
public class OrnamentResource {

    private final OrnamentService ornamentService;

    @Inject
    public OrnamentResource(OrnamentService ornamentService) {
        this.ornamentService = ornamentService;
    }

    @GET
    public Response getAllOrnaments(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<OrnamentResponse> ornaments = ornamentService.getAllOrnaments(ownerId);
        return Response.ok(ApiResponse.success(ornaments, "Ornaments retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ORNAMENT, paramName = "id")
    public Response getOrnament(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        OrnamentResponse ornament = ornamentService.getOrnament(id, ownerId);
        return Response.ok(ApiResponse.success(ornament, "Ornament retrieved successfully")).build();
    }

    @GET
    @Path("/aquarium/{aquariumId}")
    public Response getOrnamentsByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<OrnamentResponse> ornaments = ornamentService.getOrnamentsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(ornaments, "Ornaments retrieved successfully")).build();
    }

    @POST
    public Response createOrnament(OrnamentRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        OrnamentResponse ornament = ornamentService.createOrnament(request, ownerId);
        
        Map<String, Object> responseData = Map.of(
            "ornament", ornament,
            "id", ornament.id()
        );
        
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(responseData, "Ornament created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ORNAMENT, paramName = "id")
    public Response updateOrnament(@PathParam("id") Long id, OrnamentRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        OrnamentResponse ornament = ornamentService.updateOrnament(id, request, ownerId);
        return Response.ok(ApiResponse.success(ornament, "Ornament updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ORNAMENT, paramName = "id")
    public Response deleteOrnament(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        ornamentService.deleteOrnament(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Ornament deleted successfully")).build();
    }
}