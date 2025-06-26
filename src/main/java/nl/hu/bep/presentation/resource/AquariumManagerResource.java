package nl.hu.bep.presentation.resource;

import nl.hu.bep.data.*;
import nl.hu.bep.presentation.dto.mapper.EntityMapper;
import nl.hu.bep.application.factory.InhabitantFactory;
import nl.hu.bep.application.service.AquariumManagerService;
import nl.hu.bep.presentation.dto.request.AquariumRequest;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.presentation.dto.response.AquariumResponse;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.SecurityContextHelper;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
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

    // Constructor for testing with injected service
    public AquariumManagerResource(AquariumManagerService aquariumManagerService) {
        this.aquariumManagerService = aquariumManagerService;
    }

    // Fallback constructor for cases when HK2 fails - good defensive programming
    public AquariumManagerResource() {
        var aquariumRepository = new AquariumRepositoryImpl();
        var accessoryRepository = new AccessoryRepositoryImpl();
        var ornamentRepository = new OrnamentRepositoryImpl();
        var inhabitantRepository = new InhabitantRepositoryImpl();
        var ownerRepository = new OwnerRepositoryImpl();
        var entityMapper = new EntityMapper();
        var inhabitantFactory = new InhabitantFactory();
        
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

    @GET
    @Secured
    public Response getAllAquariums(@Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        List<AquariumResponse> aquariums = aquariumManagerService.getAllAquariums(ownerId);
        return Response.ok(ApiResponse.success(aquariums, "Aquariums fetched successfully")).build();
    }

    @GET
    @Path("/{id}")
    @Secured
    public Response getAquarium(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse aquarium = aquariumManagerService.getAquarium(id, ownerId);
        return Response.ok(ApiResponse.success(aquarium, "Aquarium fetched successfully")).build();
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
            @Context SecurityContext securityContext) {

        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        AquariumResponse updatedAquarium = aquariumManagerService.updateAquarium(id, request, ownerId);
        return Response.ok(ApiResponse.success(updatedAquarium, "Aquarium updated successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response deleteAquarium(@PathParam("id") Long id, @Context SecurityContext securityContext) {
        Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
        aquariumManagerService.deleteAquarium(id, ownerId);
        return Response.ok(ApiResponse.success(
                Map.of("aquariumId", id),
                "Aquarium deleted successfully")).build();
    }
}