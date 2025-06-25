package nl.hu.bep.security.application.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.config.AquariumConstants;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.security.application.annotation.Secured;
import nl.hu.bep.security.application.context.AquariumSecurityContext;
import nl.hu.bep.security.application.service.JwtService;

import java.util.HashMap;
import java.util.Map;

/**
 * Security filter for JWT authentication.
 * Refactored to use CDI for service injection following enterprise patterns.
 */
@Slf4j
@Provider
@Secured
@Priority(Priorities.AUTHENTICATION)
public class AquariumSecurityFilter implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = AquariumConstants.BEARER_SCHEME;
    private static final String[] PUBLIC_ENDPOINTS = AquariumConstants.PUBLIC_ENDPOINTS;

    private final JwtService jwtService;

    @Context
    private ResourceInfo resourceInfo;

    @Inject
    public AquariumSecurityFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        for (String publicEndpoint : PUBLIC_ENDPOINTS) {
            if (path.endsWith(publicEndpoint)) {
                return;
            }
        }

        if (!requiresAuthentication(resourceInfo)) {
            return;
        }

        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (!isValidAuthorizationHeader(authorizationHeader)) {
            abortWithUnauthorized(requestContext, "Authorization header must be provided");
            return;
        }

        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

        try {
            DecodedJWT jwt = jwtService.verifyToken(token);
            Long userId = Long.parseLong(jwt.getSubject());
            String username = jwt.getClaim("username").asString();

            requestContext.setSecurityContext(new AquariumSecurityContext(userId, username));

            log.info("Authenticated request for user: {} (ID: {})", username, userId);
        } catch (JWTVerificationException e) {
            log.error("Invalid token: {}", e.getMessage());
            abortWithUnauthorized(requestContext, "Invalid token");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("path", requestContext.getUriInfo().getPath());
            errorDetails.put("exceptionType", e.getClass().getSimpleName());

            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error(errorDetails, "Internal Server Error: " + e.getMessage()))
                    .type(MediaType.APPLICATION_JSON)
                    .build());
        }
    }

    private boolean requiresAuthentication(ResourceInfo resourceInfo) {
        var method = resourceInfo.getResourceMethod();
        return method.isAnnotationPresent(Secured.class) ||
                resourceInfo.getResourceClass().isAnnotationPresent(Secured.class);
    }

    private boolean isValidAuthorizationHeader(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext, String message) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("path", requestContext.getUriInfo().getPath());
        errorDetails.put("code", Response.Status.UNAUTHORIZED.getStatusCode());

        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.error(errorDetails, message))
                .type(MediaType.APPLICATION_JSON)
                .build());
    }
}