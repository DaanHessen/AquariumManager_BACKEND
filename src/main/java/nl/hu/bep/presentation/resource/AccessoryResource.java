package nl.hu.bep.presentation.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.presentation.dto.request.AccessoryRequest;
import nl.hu.bep.presentation.dto.response.AccessoryResponse;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.SecurityContextHelper;

import java.util.List;
import java.util.Map;

@Slf4j
@Path("/accessories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Secured
public class AccessoryResource {

    private AquariumManagerService aquariumManagerService;

    // Default constructor for Jersey/JAX-RS
    public AccessoryResource() {
        // Initialize dependencies manually since no CDI injection
        var aquariumRepository = new nl.hu.bep.data.AquariumRepositoryImpl();
        var accessoryRepository = new nl.hu.bep.data.AccessoryRepositoryImpl();
        var ornamentRepository = new nl.hu.bep.data.OrnamentRepositoryImpl();
        var inhabitantRepository = new nl.hu.bep.data.InhabitantRepositoryImpl();
        var ownerRepository = new nl.hu.bep.data.OwnerRepositoryImpl();
        var entityMapper = new nl.hu.bep.presentation.dto.mapper.EntityMapper();
        var inhabitantFactory = new nl.hu.bep.application.factory.InhabitantFactory();
        
        this.aquariumManagerService = new AquariumManagerService(
            aquariumRepository,
            accessoryRepository, 
            ornamentRepository,
            inhabitantRepository,
            ownerRepository,
            entityMapper,
            inhabitantFactory
        );
    }

    public AccessoryResource(AquariumManagerService aquariumManagerService) {
        this.aquariumManagerService = aquariumManagerService;
    }

    @GET
    public Response getAllAccessories(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<AccessoryResponse> accessories = aquariumManagerService.getAllAccessories(ownerId);
        return Response.ok(ApiResponse.success(accessories, "Accessories retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ACCESSORY, paramName = "id")
    public Response getAccessory(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AccessoryResponse accessory = aquariumManagerService.getAccessory(id, ownerId);
        return Response.ok(ApiResponse.success(accessory, "Accessory retrieved successfully")).build();
    }

    @GET
    @Path("/aquarium/{aquariumId}")
    public Response getAccessoriesByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<AccessoryResponse> accessories = aquariumManagerService.getAccessoriesByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(accessories, "Accessories retrieved successfully")).build();
    }

    @POST
    public Response createAccessory(AccessoryRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AccessoryResponse accessory = aquariumManagerService.createAccessory(request, ownerId);
        
        Map<String, Object> responseData = Map.of(
            "accessory", accessory,
            "id", accessory.id()
        );
        
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(responseData, "Accessory created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ACCESSORY, paramName = "id")
    public Response updateAccessory(@PathParam("id") Long id, AccessoryRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AccessoryResponse accessory = aquariumManagerService.updateAccessory(id, request, ownerId);
        return Response.ok(ApiResponse.success(accessory, "Accessory updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ACCESSORY, paramName = "id")
    public Response deleteAccessory(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        aquariumManagerService.deleteAccessory(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Accessory deleted successfully")).build();
    }
}