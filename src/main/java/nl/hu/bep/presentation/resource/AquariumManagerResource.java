package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.hu.bep.application.AquariumManagerService;
import nl.hu.bep.presentation.dto.*;
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
public class AquariumManagerResource {

    private final AquariumManagerService aquariumManagerService;

    @Inject
    public AquariumManagerResource(AquariumManagerService aquariumManagerService) {
        this.aquariumManagerService = aquariumManagerService;
    }

    // ========== AQUARIUM OPERATIONS ==========

    @GET
    @Secured
    public Response getAllAquariums(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        log.info("Fetching aquariums for ownerId: {}", ownerId);
        List<AquariumResponse> aquariums = aquariumManagerService.getAllAquariums(ownerId);
        return Response.ok(ApiResponse.success(aquariums, "Aquariums fetched successfully")).build();
    }

    @GET
    @Path("/{id}")
    public Response getAquariumById(@PathParam("id") Long id) {
        AquariumResponse aquarium = aquariumManagerService.getAquariumById(id);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium fetched successfully")).build();
    }

    @GET
    @Path("/{id}/detail")
    public Response getAquariumDetailById(@PathParam("id") Long id) {
        AquariumResponse aquarium = aquariumManagerService.getAquariumDetailById(id);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium detail fetched successfully")).build();
    }

    @POST
    @Secured
    public Response createAquarium(
            AquariumRequest request,
            @Context UriInfo uriInfo,
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse createdAquarium = aquariumManagerService.createAquarium(request, ownerId);
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

        AquariumResponse updatedAquarium = aquariumManagerService.updateAquarium(id, request);
        return Response.ok(ApiResponse.success(updatedAquarium, "Aquarium updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.AQUARIUM, paramName = "id")
    public Response deleteAquarium(@PathParam("id") Long id, @Context UriInfo uriInfo) {
        aquariumManagerService.deleteAquarium(id);
        return Response.ok(ApiResponse.success(
                Map.of("aquariumId", id),
                "Aquarium deleted successfully")).build();
    }

    // ========== STATE HISTORY OPERATIONS ==========

    @GET
    @Path("/{id}/state-history")
    public Response getAquariumStateHistory(@PathParam("id") Long aquariumId) {
        List<AquariumStateHistoryResponse> stateHistory = aquariumManagerService.getAquariumStateHistory(aquariumId);
        return Response.ok(ApiResponse.success(stateHistory, "State history fetched successfully")).build();
    }

    @GET
    @Path("/{id}/current-state-duration")
    public Response getCurrentStateDuration(@PathParam("id") Long aquariumId) {
        Long duration = aquariumManagerService.getCurrentStateDuration(aquariumId);
        return Response.ok(ApiResponse.success(
                Map.of("aquariumId", aquariumId, "currentStateDurationMinutes", duration),
                "Current state duration fetched successfully")).build();
    }

    // ========== ACCESSORY OPERATIONS ==========

    @GET
    @Path("/accessories")
    @Secured
    public Response getAllAccessories(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<AccessoryResponse> accessories = aquariumManagerService.getAllAccessories(ownerId);
        return Response.ok(ApiResponse.success(accessories, "Accessories fetched successfully")).build();
    }

    @GET
    @Path("/accessories/{id}")
    public Response getAccessoryById(@PathParam("id") Long id) {
        AccessoryResponse accessory = aquariumManagerService.getAccessoryById(id);
        return Response.ok(ApiResponse.success(accessory, "Accessory fetched successfully")).build();
    }

    @GET
    @Path("/{aquariumId}/accessories")
    public Response getAccessoriesByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<AccessoryResponse> accessories = aquariumManagerService.getAccessoriesByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(accessories, "Aquarium accessories fetched successfully")).build();
    }

    @DELETE
    @Path("/accessories/{id}")
    @Secured
    @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ACCESSORY, paramName = "id")
    public Response deleteAccessory(@PathParam("id") Long id) {
        aquariumManagerService.deleteAccessory(id);
        return Response.ok(ApiResponse.success(
                Map.of("accessoryId", id),
                "Accessory deleted successfully")).build();
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
        AquariumResponse updatedAquarium = aquariumManagerService.addAccessoryToAquarium(aquariumId, accessoryId, properties, ownerId);
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
        AquariumResponse updatedAquarium = aquariumManagerService.removeAccessoryFromAquarium(aquariumId, accessoryId, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Accessory removed successfully")).build();
    }

    // ========== ORNAMENT OPERATIONS ==========

    @GET
    @Path("/ornaments")
    @Secured
    public Response getAllOrnaments(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<OrnamentResponse> ornaments = aquariumManagerService.getAllOrnaments(ownerId);
        return Response.ok(ApiResponse.success(ornaments, "Ornaments fetched successfully")).build();
    }

    @GET
    @Path("/ornaments/{id}")
    public Response getOrnamentById(@PathParam("id") Long id) {
        OrnamentResponse ornament = aquariumManagerService.getOrnamentById(id);
        return Response.ok(ApiResponse.success(ornament, "Ornament fetched successfully")).build();
    }

    @GET
    @Path("/{aquariumId}/ornaments")
    public Response getOrnamentsByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<OrnamentResponse> ornaments = aquariumManagerService.getOrnamentsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(ornaments, "Aquarium ornaments fetched successfully")).build();
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
        AquariumResponse updatedAquarium = aquariumManagerService.addOrnamentToAquarium(aquariumId, ornamentId, properties, ownerId);
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
        AquariumResponse updatedAquarium = aquariumManagerService.removeOrnamentFromAquarium(aquariumId, ornamentId, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Ornament removed successfully")).build();
    }

    // ========== INHABITANT OPERATIONS ==========

    @GET
    @Path("/inhabitants")
    @Secured
    public Response getAllInhabitants(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<InhabitantResponse> inhabitants = aquariumManagerService.getAllInhabitants(ownerId);
        return Response.ok(ApiResponse.success(inhabitants, "Inhabitants fetched successfully")).build();
    }

    @GET
    @Path("/inhabitants/{id}")
    public Response getInhabitantById(@PathParam("id") Long id) {
        InhabitantResponse inhabitant = aquariumManagerService.getInhabitantById(id);
        return Response.ok(ApiResponse.success(inhabitant, "Inhabitant fetched successfully")).build();
    }

    @GET
    @Path("/{aquariumId}/inhabitants")
    public Response getInhabitantsByAquarium(@PathParam("aquariumId") Long aquariumId) {
        List<InhabitantResponse> inhabitants = aquariumManagerService.getInhabitantsByAquarium(aquariumId);
        return Response.ok(ApiResponse.success(inhabitants, "Aquarium inhabitants fetched successfully")).build();
    }

    // Note: Inhabitant add/remove operations would be added here following the same pattern
    // as accessories and ornaments, but are not implemented yet in the service layer
} 