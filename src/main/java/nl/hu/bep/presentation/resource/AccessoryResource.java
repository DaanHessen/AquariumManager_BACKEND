package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Context;
import nl.hu.bep.application.AquariumManagerService;
import nl.hu.bep.presentation.dto.*;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.SecurityContextHelper;

import java.util.List;

@Path("/accessories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class AccessoryResource {

    @Inject
    private AquariumManagerService aquariumManagerService;

    @GET
    public Response getAllAccessories(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<AccessoryResponse> accessories = aquariumManagerService.getAllAccessories(ownerId);
        return Response.ok(ApiResponse.success(accessories, "Accessories retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    public Response getAccessoryById(@PathParam("id") Long id) {
        AccessoryResponse accessory = aquariumManagerService.getAccessoryById(id);
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
        AccessoryResponse createdAccessory = aquariumManagerService.createAccessory(request, ownerId);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(createdAccessory, "Accessory created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAccessory(@PathParam("id") Long id, AccessoryRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AccessoryResponse updatedAccessory = aquariumManagerService.updateAccessory(id, request, ownerId);
        return Response.ok(ApiResponse.success(updatedAccessory, "Accessory updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAccessory(@PathParam("id") Long id) {
        aquariumManagerService.deleteAccessory(id);
        return Response.ok(ApiResponse.success(null, "Accessory deleted successfully")).build();
    }
}