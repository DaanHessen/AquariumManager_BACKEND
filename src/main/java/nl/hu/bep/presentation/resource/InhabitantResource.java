package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import nl.hu.bep.application.service.InhabitantService;
import nl.hu.bep.presentation.dto.request.InhabitantRequest;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.presentation.dto.response.InhabitantResponse;
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
@Path("/inhabitants")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Secured
public class InhabitantResource {

    private final InhabitantService inhabitantService;

    @Inject
    public InhabitantResource(InhabitantService inhabitantService) {
        this.inhabitantService = inhabitantService;
    }

    @GET
    public Response getAllInhabitants(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<InhabitantResponse> inhabitants = inhabitantService.getAllInhabitants(ownerId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response getInhabitant(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse inhabitant = inhabitantService.getInhabitant(id, ownerId);
        return Response.ok(ApiResponse.success(inhabitant, "Inhabitant retrieved successfully")).build();
    }

    @GET
    @Path("/aquarium/{aquariumId}")
    public Response getInhabitantsByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<InhabitantResponse> inhabitants = inhabitantService.getInhabitantsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants retrieved successfully")).build();
    }

    @POST
    public Response createInhabitant(InhabitantRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse inhabitant = inhabitantService.createInhabitant(request, ownerId);
        
        Map<String, Object> responseData = Map.of(
            "inhabitant", inhabitant,
            "id", inhabitant.id()
        );
        
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(responseData, "Inhabitant created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response updateInhabitant(@PathParam("id") Long id, InhabitantRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse inhabitant = inhabitantService.updateInhabitant(id, request, ownerId);
        return Response.ok(ApiResponse.success(inhabitant, "Inhabitant updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response deleteInhabitant(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        inhabitantService.deleteInhabitant(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Inhabitant deleted successfully")).build();
    }
}