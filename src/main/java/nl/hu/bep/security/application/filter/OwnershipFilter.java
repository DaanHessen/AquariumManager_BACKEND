package nl.hu.bep.security.application.filter;

import nl.hu.bep.exception.ApplicationException.BusinessRuleException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Map;

import nl.hu.bep.data.interfaces.AquariumRepository;
import nl.hu.bep.data.interfaces.AccessoryRepository;
import nl.hu.bep.data.interfaces.InhabitantRepository;
import nl.hu.bep.data.interfaces.OrnamentRepository;
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.context.SecurityContextHelper;

@Slf4j
@Provider
@RequiresOwnership.Checker
@Priority(Priorities.AUTHORIZATION)
public class OwnershipFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    private final AquariumRepository aquariumRepository;
    private final AccessoryRepository accessoryRepository;
    private final InhabitantRepository inhabitantRepository;
    private final OrnamentRepository ornamentRepository;

    @Inject
    public OwnershipFilter(AquariumRepository aquariumRepository, AccessoryRepository accessoryRepository, InhabitantRepository inhabitantRepository, OrnamentRepository ornamentRepository) {
        this.aquariumRepository = aquariumRepository;
        this.accessoryRepository = accessoryRepository;
        this.inhabitantRepository = inhabitantRepository;
        this.ornamentRepository = ornamentRepository;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        var method = resourceInfo.getResourceMethod();

        RequiresOwnership ownershipAnnotation = method.getAnnotation(RequiresOwnership.class);
        if (ownershipAnnotation == null) {
            return;
        }

        try {
            Long authenticatedOwnerId = SecurityContextHelper
                    .getAuthenticatedOwnerId(requestContext.getSecurityContext());

            RequiresOwnership.ResourceType resourceType = ownershipAnnotation.resourceType();
            String paramName = ownershipAnnotation.paramName();
            Long resourceId = extractResourceIdFromPath(paramName, requestContext);

            if (resourceId == null) {
                log.error("Failed to extract resource ID for parameter: {}", paramName);
                abortWithUnauthorized(requestContext, "Unable to verify ownership. Access denied.");
                return;
            }

            boolean isOwner = switch (resourceType) {
                case AQUARIUM -> verifyAquariumOwnership(resourceId, authenticatedOwnerId);
                case INHABITANT -> verifyInhabitantOwnership(resourceId, authenticatedOwnerId);
                case ACCESSORY -> verifyAccessoryOwnership(resourceId, authenticatedOwnerId);
                case ORNAMENT -> verifyOrnamentOwnership(resourceId, authenticatedOwnerId);
                default -> false;
            };

            if (!isOwner) {
                log.warn("Ownership check failed for owner {} on {} with ID {}",
                        authenticatedOwnerId, resourceType, resourceId);
                abortWithUnauthorized(requestContext, "You are not authorized to access this resource.");
                return;
            }

            log.info("Ownership verified for owner {} on {} with ID {}",
                    authenticatedOwnerId, resourceType, resourceId);
        } catch (Exception e) {
            log.error("Error during ownership verification: {}", e.getMessage());
            abortWithUnauthorized(requestContext, "Unable to verify ownership. Access denied.");
        }
    }

    private Long extractResourceIdFromPath(String paramName, ContainerRequestContext requestContext) {
        try {
            String pathParamValue = requestContext.getUriInfo()
                    .getPathParameters()
                    .getFirst(paramName);

            if (pathParamValue != null) {
                return Long.parseLong(pathParamValue);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid resource ID format for parameter {}", paramName);
            throw new BusinessRuleException("Invalid resource ID format");
        }

        throw new BusinessRuleException("Resource ID parameter not found: " + paramName);
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of("error", message))
                        .build());
    }

    private boolean verifyAquariumOwnership(Long aquariumId, Long ownerId) {
        try {
            var aquarium = aquariumRepository.findById(aquariumId).orElse(null);
            if (aquarium == null) return false;
            
            aquarium.validateOwnership(ownerId);
            return true;
        } catch (Exception e) {
            log.debug("Aquarium ownership verification failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean verifyInhabitantOwnership(Long inhabitantId, Long ownerId) {
        try {
            var inhabitant = inhabitantRepository.findById(inhabitantId).orElse(null);
            if (inhabitant == null) return false;
            
            inhabitant.validateOwnership(ownerId);
            return true;
        } catch (Exception e) {
            log.debug("Inhabitant ownership verification failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean verifyAccessoryOwnership(Long accessoryId, Long ownerId) {
        try {
            var accessory = accessoryRepository.findById(accessoryId).orElse(null);
            if (accessory == null) return false;
            
            return accessory.getOwnerId().equals(ownerId);
        } catch (Exception e) {
            log.debug("Accessory ownership verification failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean verifyOrnamentOwnership(Long ornamentId, Long ownerId) {
        try {
            var ornament = ornamentRepository.findById(ornamentId).orElse(null);
            if (ornament == null) return false;
            
            ornament.validateOwnership(ownerId);
            return true;
        } catch (Exception e) {
            log.debug("Ornament ownership verification failed: {}", e.getMessage());
            return false;
        }
    }
}