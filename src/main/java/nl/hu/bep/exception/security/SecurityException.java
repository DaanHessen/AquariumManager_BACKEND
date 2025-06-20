package nl.hu.bep.exception.security;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.exception.core.AquariumException;

import java.util.Map;

/**
 * Security layer exceptions for authentication, authorization, and access control.
 * Handles JWT token validation, user authentication, and permission violations.
 */
public class SecurityException extends AquariumException {

    public SecurityException(String message) {
        super(message, Response.Status.UNAUTHORIZED, "SECURITY_ERROR");
    }

    public SecurityException(String message, Throwable cause) {
        super(message, Response.Status.UNAUTHORIZED, "SECURITY_ERROR", cause);
    }

    public SecurityException(String message, Response.Status status, String errorCode) {
        super(message, status, errorCode);
    }

    public SecurityException(String message, Response.Status status, String errorCode, Map<String, Object> details) {
        super(message, status, errorCode, null, details);
    }

    // ========== SPECIFIC SECURITY EXCEPTIONS ==========

    /**
     * Authentication failures (invalid credentials, expired tokens, etc.).
     */
    public static class AuthenticationException extends SecurityException {
        public AuthenticationException(String message) {
            super(message, Response.Status.UNAUTHORIZED, "AUTHENTICATION_FAILED");
        }

        public AuthenticationException(String message, Map<String, Object> details) {
            super(message, Response.Status.UNAUTHORIZED, "AUTHENTICATION_FAILED", details);
        }
    }

    /**
     * Authorization failures (insufficient permissions, access denied).
     */
    public static class AuthorizationException extends SecurityException {
        public AuthorizationException(String message) {
            super(message, Response.Status.FORBIDDEN, "AUTHORIZATION_FAILED");
        }

        public AuthorizationException(String resource, String action) {
            super(String.format("Access denied: insufficient permissions for %s on %s", action, resource),
                  Response.Status.FORBIDDEN, "AUTHORIZATION_FAILED",
                  Map.of("resource", resource, "action", action));
        }
    }

    /**
     * JWT token validation and processing errors.
     */
    public static class TokenException extends SecurityException {
        public TokenException(String message) {
            super(message, Response.Status.UNAUTHORIZED, "TOKEN_ERROR");
        }

        public TokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Resource ownership violations.
     */
    public static class OwnershipException extends SecurityException {
        public OwnershipException(String entityType, Long entityId, Long ownerId) {
            super(String.format("Ownership violation: %s %d does not belong to owner %d", 
                    entityType, entityId, ownerId),
                  Response.Status.FORBIDDEN, "OWNERSHIP_VIOLATION",
                  Map.of("entityType", entityType, "entityId", entityId, "ownerId", ownerId));
        }
    }
} 