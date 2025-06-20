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

@Path("/inhabitants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class InhabitantResource {

    @Inject
    private AquariumManagerService aquariumManagerService;

    @GET
    public Response getAllInhabitants(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<InhabitantResponse> inhabitants = aquariumManagerService.getAllInhabitants(ownerId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants retrieved successfully")).build();
    }

    @GET
    @Path("/{id}")
    public Response getInhabitantById(@PathParam("id") Long id) {
        InhabitantResponse inhabitant = aquariumManagerService.getInhabitantById(id);
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
        InhabitantResponse createdInhabitant = aquariumManagerService.createInhabitant(request, ownerId);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success(createdInhabitant, "Inhabitant created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateInhabitant(@PathParam("id") Long id, InhabitantRequest request, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse updatedInhabitant = aquariumManagerService.updateInhabitant(id, request, ownerId);
        return Response.ok(ApiResponse.success(updatedInhabitant, "Inhabitant updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteInhabitant(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        aquariumManagerService.deleteInhabitant(id, ownerId);
        return Response.ok(ApiResponse.success(null, "Inhabitant deleted successfully")).build();
    }
}