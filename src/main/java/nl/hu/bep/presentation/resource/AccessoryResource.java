package nl.hu.bep.presentation.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.hu.bep.application.AccessoryService;
import nl.hu.bep.presentation.dto.AccessoryRequest;
import nl.hu.bep.presentation.dto.AccessoryResponse;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.SecurityContextHelper;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.core.SecurityContext;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Path("/accessories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class AccessoryResource {

  private final AccessoryService accessoryService;

  @Inject
  public AccessoryResource(AccessoryService accessoryService) {
    this.accessoryService = accessoryService;
  }

  @GET
  @Secured
  public Response getAllAccessories(@Context SecurityContext securityContext) {
    Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
    log.info("Fetching accessories for ownerId: {}", ownerId);
    List<AccessoryResponse> accessories = accessoryService.getAllAccessories(ownerId);
    return Response.ok(ApiResponse.success(accessories, "Accessories fetched successfully")).build();
  }

  @GET
  @Path("/{id}")
  public Response getAccessoryById(@PathParam("id") Long id) {
    AccessoryResponse accessory = accessoryService.getAccessoryById(id);
    return Response.ok(ApiResponse.success(accessory, "Accessory fetched successfully")).build();
  }

  @GET
  @Path("/byAquarium/{aquariumId}")
  public Response getAccessoriesByAquarium(@PathParam("aquariumId") Long aquariumId) {
    List<AccessoryResponse> accessories = accessoryService.getAccessoriesByAquarium(aquariumId);
    return Response.ok(ApiResponse.success(accessories, "Accessories fetched successfully")).build();
  }

  @POST
  @Secured
  public Response createAccessory(
      AccessoryRequest request,
      @Context UriInfo uriInfo,
      @Context SecurityContext securityContext) {

    Long ownerId = SecurityContextHelper.getAuthenticatedOwnerId(securityContext);
    AccessoryResponse createdAccessory = accessoryService.createAccessory(request, ownerId);
    URI location = uriInfo.getAbsolutePathBuilder().path(createdAccessory.id().toString()).build();
    return Response.created(location)
        .entity(ApiResponse.success(createdAccessory, "Accessory created successfully"))
        .build();
  }

  @PUT
  @Path("/{id}")
  @Secured
  @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ACCESSORY, paramName = "id")
  public Response updateAccessory(
      @PathParam("id") Long id,
      AccessoryRequest request) {

    AccessoryResponse updatedAccessory = accessoryService.updateAccessory(id, request);
    return Response.ok(ApiResponse.success(updatedAccessory, "Accessory updated successfully")).build();
  }

  @DELETE
  @Path("/{id}")
  @Secured
  @RequiresOwnership(resourceType = RequiresOwnership.ResourceType.ACCESSORY, paramName = "id")
  public Response deleteAccessory(@PathParam("id") Long id) {
    accessoryService.deleteAccessory(id);
    return Response.ok(ApiResponse.success(
        Map.of("accessoryId", id),
        "Accessory deleted successfully")).build();
  }
}