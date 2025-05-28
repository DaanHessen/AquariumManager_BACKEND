package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.hu.bep.application.InhabitantService;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.presentation.dto.InhabitantRequest;
import nl.hu.bep.presentation.dto.InhabitantResponse;
import nl.hu.bep.security.application.context.SecurityContextHelper;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.annotation.Secured;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.core.SecurityContext;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/inhabitants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class InhabitantResource {

    private final InhabitantService inhabitantService;

    @Inject
    public InhabitantResource(InhabitantService inhabitantService) {
        this.inhabitantService = inhabitantService;
    }

    @GET
    @Secured
    public Response getAllInhabitants(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        log.info("Fetching inhabitants for ownerId: {}", ownerId);
        List<InhabitantResponse> inhabitants = inhabitantService.getAllInhabitants(ownerId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants fetched successfully")).build();
    }

    @GET
    @Path("/{id}")
    public Response getInhabitantById(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        InhabitantResponse inhabitant = inhabitantService.getInhabitantById(id);
        return Response.ok(ApiResponse.success(inhabitant, "Inhabitant fetched successfully")).build();
    }

    @GET
    @Path("/byAquarium/{aquariumId}")
    public Response getInhabitantsByAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @Context UriInfo uriInfo) {
        List<InhabitantResponse> inhabitants = inhabitantService.getInhabitantsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants fetched successfully")).build();
    }

    @POST
    @Secured
    public Response createInhabitant(
            InhabitantRequest request,
            @Context UriInfo uriInfo,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        InhabitantResponse createdInhabitant = inhabitantService.createInhabitant(request, ownerId);
        URI location = uriInfo.getAbsolutePathBuilder().path(createdInhabitant.id().toString()).build();
        return Response.created(location)
                .entity(ApiResponse.success(createdInhabitant, "Inhabitant created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response updateInhabitant(
            @PathParam("id") Long id,
            InhabitantRequest request,
            @Context UriInfo uriInfo) {

        InhabitantResponse updatedInhabitant = inhabitantService.updateInhabitant(id, request);
        return Response.ok(ApiResponse.success(updatedInhabitant, "Inhabitant updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.INHABITANT, paramName = "id")
    public Response deleteInhabitant(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        inhabitantService.deleteInhabitant(id);
        return Response.ok(ApiResponse.success(
                Map.of("inhabitantId", id),
                "Inhabitant deleted successfully")).build();
    }
}