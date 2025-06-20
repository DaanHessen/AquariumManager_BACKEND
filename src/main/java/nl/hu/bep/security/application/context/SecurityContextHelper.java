package nl.hu.bep.security.application.context;

import jakarta.ws.rs.core.SecurityContext;
import nl.hu.bep.exception.security.SecurityException;

public class SecurityContextHelper {

    public static Long getAuthenticatedOwnerId(SecurityContext securityContext) {
        if (securityContext == null) {
            throw new SecurityException.AuthenticationException("Security context is null. Authentication required.");
        }

        if (securityContext.getUserPrincipal() == null) {
            throw new SecurityException.AuthenticationException("Owner is not authenticated.");
        }

        if (securityContext instanceof AquariumSecurityContext) {
            return ((AquariumSecurityContext) securityContext).getOwnerId();
        } else {
            try {
                return Long.parseLong(securityContext.getUserPrincipal().getName());
            } catch (NumberFormatException e) {
                throw new SecurityException.AuthenticationException(
                        "Invalid security context. Cannot extract owner ID.");
            }
        }
    }

    public static String getAuthenticatedUsername(SecurityContext securityContext) {
        if (securityContext == null) {
            throw new SecurityException.AuthenticationException("Security context is null. Authentication required.");
        }

        if (securityContext.getUserPrincipal() == null) {
            throw new SecurityException.AuthenticationException("Owner is not authenticated.");
        }

        if (securityContext instanceof AquariumSecurityContext) {
            return ((AquariumSecurityContext) securityContext).getUsername();
        } else {
            return securityContext.getUserPrincipal().getName();
        }
    }
}