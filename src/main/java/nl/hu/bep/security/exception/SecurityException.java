package nl.hu.bep.security.exception;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.presentation.exception.BaseException;

public class SecurityException extends BaseException {

    public SecurityException(String message) {
        super(message, Response.Status.UNAUTHORIZED);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause, Response.Status.UNAUTHORIZED);
    }

    public static class AuthenticationException extends SecurityException {
        public AuthenticationException(String message) {
            super(message);
        }

        public AuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class AuthorizationException extends SecurityException {
        public AuthorizationException(String message) {
            super(message);
        }

        public AuthorizationException(String message, Throwable cause) {
            super(message, cause);
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.FORBIDDEN;
        }
    }

    public static class TokenException extends SecurityException {
        public TokenException(String message) {
            super(message);
        }

        public TokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class OwnershipException extends SecurityException {
        public OwnershipException(String message) {
            super(message);
        }

        public OwnershipException(String message, Throwable cause) {
            super(message, cause);
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.FORBIDDEN;
        }
    }
}