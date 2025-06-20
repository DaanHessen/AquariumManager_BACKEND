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

@Path("/ornaments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class OrnamentResource {

    @Inject
    private AquariumManagerService aquariumManagerService;

    @GET
    public Response getAllOrnaments(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<OrnamentResponse> ornaments = aquariumManagerService.getAllOrnaments(ownerId);
        return Response.ok(ApiResponse.success(ornaments, "Ornaments retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrnamentById(@PathParam("id") Long id) {
        OrnamentResponse ornament = aquariumManagerService.getOrnamentById(id);
        return Response.ok(ApiResponse.success(ornament, "Ornament retrieved successfully")).build();
    }

    @GET
    @Path("/aquarium/{aquariumId}")
    public Response getOrnamentsByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<OrnamentResponse> ornaments = aquariumManagerService.getOrnamentsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(ornaments, "Ornaments retrieved successfully")).build();
    }

    @POST
    public Response createOrnament(OrnamentRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        OrnamentResponse createdOrnament = aquariumManagerService.createOrnament(request, ownerId);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(createdOrnament, "Ornament created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateOrnament(@PathParam("id") Long id, OrnamentRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        OrnamentResponse updatedOrnament = aquariumManagerService.updateOrnament(id, request, ownerId);
        return Response.ok(ApiResponse.success(updatedOrnament, "Ornament updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOrnament(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        aquariumManagerService.deleteOrnament(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Ornament deleted successfully")).build();
    }
}