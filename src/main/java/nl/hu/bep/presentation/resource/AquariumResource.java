package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.hu.bep.application.AquariumService;
import nl.hu.bep.application.InhabitantService;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.presentation.dto.AquariumRequest;
import nl.hu.bep.presentation.dto.AquariumResponse;
import nl.hu.bep.security.application.context.SecurityContextHelper;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.annotation.Secured;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/aquariums")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class AquariumResource {

    private final AquariumService aquariumService;
    private final InhabitantService inhabitantService;

    @Inject
    public AquariumResource(
            AquariumService aquariumService,
            InhabitantService inhabitantService) {
        this.aquariumService = aquariumService;
        this.inhabitantService = inhabitantService;
    }

    @GET
    @Secured
    public Response getAllAquariums(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        log.info("Fetching aquariums for ownerId: {}", ownerId);
        List<AquariumResponse> aquariums = aquariumService.getAllAquariums(ownerId);
        return Response.ok(ApiResponse.success(aquariums, "Aquariums fetched successfully")).build();
    }

    @GET
    @Path("/{id}")
    public Response getAquariumById(@PathParam("id") Long id) {
        AquariumResponse aquarium = aquariumService.getAquariumById(id);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium fetched successfully")).build();
    }

    @GET
    @Path("/{id}/detail")
    public Response getAquariumDetailById(@PathParam("id") Long id) {
        AquariumResponse aquarium = aquariumService.getAquariumDetailById(id);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium detail fetched successfully")).build();
    }

    @POST
    @Secured
    public Response createAquarium(
            AquariumRequest request,
            @Context UriInfo uriInfo,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse createdAquarium = aquariumService.createAquarium(request, ownerId);
        URI location = uriInfo.getAbsolutePathBuilder().path(createdAquarium.id().toString()).build();
        return Response.created(location)
                .entity(ApiResponse.success(createdAquarium, "Aquarium created successfully"))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Secured
    public Response updateAquarium(
            @PathParam("id") Long id,
            AquariumRequest request,
            @Context UriInfo uriInfo) {

        AquariumResponse updatedAquarium = aquariumService.updateAquarium(id, request);
        return Response.ok(ApiResponse.success(updatedAquarium, "Aquarium updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "id")
    public Response deleteAquarium(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        aquariumService.deleteAquarium(id);
        return Response.ok(ApiResponse.success(
                Map.of("aquariumId", id),
                "Aquarium deleted successfully")).build();
    }

    @POST
    @Path("/{aquariumId}/accessories/{accessoryId}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "aquariumId")
    public Response addAccessoryToAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @PathParam("accessoryId") Long accessoryId,
            Map<String, Object> properties,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = aquariumService.addAccessory(aquariumId, accessoryId, properties, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Accessory added successfully")).build();
    }

    @DELETE
    @Path("/{aquariumId}/accessories/{accessoryId}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "aquariumId")
    public Response removeAccessoryFromAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @PathParam("accessoryId") Long accessoryId,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = aquariumService.removeAccessory(aquariumId, accessoryId, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Accessory removed successfully")).build();
    }

    @POST
    @Path("/{aquariumId}/ornaments/{ornamentId}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "aquariumId")
    public Response addOrnamentToAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @PathParam("ornamentId") Long ornamentId,
            Map<String, Object> properties,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = aquariumService.addOrnament(aquariumId, ornamentId, properties, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Ornament added successfully")).build();
    }

    @DELETE
    @Path("/{aquariumId}/ornaments/{ornamentId}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "aquariumId")
    public Response removeOrnamentFromAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @PathParam("ornamentId") Long ornamentId,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = aquariumService.removeOrnament(aquariumId, ornamentId, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Ornament removed successfully")).build();
    }

    @POST
    @Path("/{aquariumId}/inhabitants/{inhabitantId}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "aquariumId")
    public Response addInhabitantToAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @PathParam("inhabitantId") Long inhabitantId,
            Map<String, Object> properties,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = inhabitantService.addInhabitant(aquariumId, inhabitantId, properties,
                ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Inhabitant added successfully")).build();
    }

    @DELETE
    @Path("/{aquariumId}/inhabitants/{inhabitantId}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "aquariumId")
    public Response removeInhabitantFromAquarium(
            @PathParam("aquariumId") Long aquariumId,
            @PathParam("inhabitantId") Long inhabitantId,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = inhabitantService.removeInhabitant(aquariumId, inhabitantId, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Inhabitant removed successfully")).build();
    }
}