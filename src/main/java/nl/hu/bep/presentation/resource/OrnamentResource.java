package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.hu.bep.application.OrnamentService;
import nl.hu.bep.presentation.dto.OrnamentRequest;
import nl.hu.bep.presentation.dto.OrnamentResponse;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.SecurityContextHelper;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.core.SecurityContext;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/ornaments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class OrnamentResource {

  private final OrnamentService ornamentService;

  @Inject
  public OrnamentResource(OrnamentService ornamentService) {
    this.ornamentService = ornamentService;
  }

  @GET
  @Secured
  public Response getAllOrnaments(@Context SecurityContext securityContext) {
    Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
    log.info("Fetching ornaments for ownerId: {}", ownerId);
    List<OrnamentResponse> ornaments = ornamentService.getAllOrnaments(ownerId);
    return Response.ok(ApiResponse.success(ornaments, "Ornaments fetched successfully")).build();
  }

  @GET
  @Path("/{id}")
  public Response getOrnamentById(@PathParam("id") Long id) {
    OrnamentResponse ornament = ornamentService.getOrnamentById(id);
    return Response.ok(ApiResponse.success(ornament, "Ornament fetched successfully")).build();
  }

  @GET
  @Path("/byAquarium/{aquariumId}")
  public Response getOrnamentsByAquarium(@PathParam("aquariumId") Long aquariumId) {
    List<OrnamentResponse> ornaments = ornamentService.getOrnamentsByAquarium(aquariumId);
    return Response.ok(ApiResponse.success(ornaments, "Ornaments fetched successfully")).build();
  }

  @POST
  @Secured
  public Response createOrnament(
      OrnamentRequest request,
      @Context UriInfo uriInfo,
      @Context SecurityContext securityContext) {

    Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
    OrnamentResponse createdOrnament = ornamentService.createOrnament(request, ownerId);
    URI location = uriInfo.getAbsolutePathBuilder().path(createdOrnament.id().toString()).build();
    return Response.created(location)
        .entity(ApiResponse.success(createdOrnament, "Ornament created successfully"))
        .build();
  }

  @PUT
  @Path("/{id}")
  @Secured
  @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ORNAMENT, paramName = "id")
  public Response updateOrnament(
      @PathParam("id") Long id,
      OrnamentRequest request) {

    OrnamentResponse updatedOrnament = ornamentService.updateOrnament(id, request);
    return Response.ok(ApiResponse.success(updatedOrnament, "Ornament updated successfully")).build();
  }

  @DELETE
  @Path("/{id}")
  @Secured
  @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ORNAMENT, paramName = "id")
  public Response deleteOrnament(@PathParam("id") Long id) {
    ornamentService.deleteOrnament(id);
    return Response.ok(ApiResponse.success(
        Map.of("ornamentId", id),
        "Ornament deleted successfully")).build();
  }
}