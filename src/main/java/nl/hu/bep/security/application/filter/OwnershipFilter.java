package nl.hu.bep.security.application.filter;

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
import nl.hu.bep.security.application.annotation.RequiresOwnership;
import nl.hu.bep.security.application.context.SecurityContextHelper;
import nl.hu.bep.security.application.service.AuthorizationService;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@Slf4j
@Provider
@RequiresOwnership.Checker
@Priority(Priorities.AUTHORIZATION)
public class OwnershipFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    private AuthorizationService authorizationService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method method = resourceInfo.getResourceMethod();

        RequiresOwnership ownershipAnnotation = method.getAnnotation(RequiresOwnership.class);
        if (ownershipAnnotation == null) {
            ownershipAnnotation = method.getDeclaringClass().getAnnotation(RequiresOwnership.class);
        }

        if (ownershipAnnotation == null) {
            return;
        }

        try {
            Long authenticatedOwnerId = SecurityContextHelper
                    .getAuthenticatedOwnerId(requestContext.getSecurityContext());

            RequiresOwnership.ResourceType resourceType = ownershipAnnotation.resourceType();
            String paramName = ownershipAnnotation.paramName();
            Long resourceId = extractResourceId(method, paramName, requestContext);

            if (resourceId == null) {
                log.error("Failed to extract resource ID for parameter: {}", paramName);
                abortWithUnauthorized(requestContext, "Unable to verify ownership. Access denied.");
                return;
            }

            boolean isOwner = switch (resourceType) {
                case AQUARIUM -> authorizationService.verifyAquariumOwnership(resourceId, authenticatedOwnerId);
                case INHABITANT -> authorizationService.verifyInhabitantOwnership(resourceId, authenticatedOwnerId);
                case ACCESSORY -> authorizationService.verifyAccessoryOwnership(resourceId, authenticatedOwnerId);
                case ORNAMENT -> authorizationService.verifyOrnamentOwnership(resourceId, authenticatedOwnerId);
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

    private Long extractResourceId(Method method, String paramName, ContainerRequestContext requestContext) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                try {
                    String pathParamValue = requestContext.getUriInfo()
                            .getPathParameters()
                            .getFirst(paramName);

                    if (pathParamValue != null) {
                        return Long.parseLong(pathParamValue);
                    }
                } catch (NumberFormatException e) {
                    log.error("Invalid resource ID format for parameter {}", paramName);
                    throw new IllegalArgumentException("Invalid resource ID format");
                }
            }
        }

        throw new IllegalArgumentException("Resource ID parameter not found: " + paramName);
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of("error", message))
                        .build());
    }
}