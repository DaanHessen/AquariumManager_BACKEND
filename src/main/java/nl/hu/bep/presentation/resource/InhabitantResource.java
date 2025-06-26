package nl.hu.bep.presentation.resource;

import nl.hu.bep.application.service.AquariumManagerService;
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

    private AquariumManagerService aquariumManagerService;

    // Default constructor for Jersey/JAX-RS
    public InhabitantResource() {
        // Initialize dependencies manually since no CDI injection
        var aquariumRepository = new nl.hu.bep.data.AquariumRepositoryImpl();
        var accessoryRepository = new nl.hu.bep.data.AccessoryRepositoryImpl();
        var ornamentRepository = new nl.hu.bep.data.OrnamentRepositoryImpl();
        var inhabitantRepository = new nl.hu.bep.data.InhabitantRepositoryImpl();
        var ownerRepository = new nl.hu.bep.data.OwnerRepositoryImpl();
        var entityMapper = new nl.hu.bep.presentation.dto.mapper.EntityMapper();
        
        this.aquariumManagerService = new AquariumManagerService(
            aquariumRepository,
            accessoryRepository, 
            ornamentRepository,
            inhabitantRepository,
            ownerRepository,
            entityMapper
        );
    }

    public InhabitantResource(AquariumManagerService aquariumManagerService) {
        this.aquariumManagerService = aquariumManagerService;
    }

    @GET
    public Response getAllInhabitants(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<InhabitantResponse> inhabitants = aquariumManagerService.getAllInhabitants(ownerId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response getInhabitant(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse inhabitant = aquariumManagerService.getInhabitant(id, ownerId);
        return Response.ok(ApiResponse.success(inhabitant, "Inhabitant retrieved successfully")).build();
    }

    @GET
    @Path("/aquarium/{aquariumId}")
    public Response getInhabitantsByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<InhabitantResponse> inhabitants = aquariumManagerService.getInhabitantsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants retrieved successfully")).build();
    }

    @POST
    public Response createInhabitant(InhabitantRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse inhabitant = aquariumManagerService.createInhabitant(request, ownerId);
        
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
        InhabitantResponse inhabitant = aquariumManagerService.updateInhabitant(id, request, ownerId);
        return Response.ok(ApiResponse.success(inhabitant, "Inhabitant updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response deleteInhabitant(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        aquariumManagerService.deleteInhabitant(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Inhabitant deleted successfully")).build();
    }
}