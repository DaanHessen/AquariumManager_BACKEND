package nl.hu.bep.exception;

public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    // resource not found errors
    public static class NotFoundException extends ApplicationException {
        public NotFoundException(String message) {
            super(message);
        }

        public NotFoundException(String resourceType, Long id) {
            super(resourceType + " with ID " + id + " not found");
        }
    }

    // input validation errors
    public static class ValidationException extends ApplicationException {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // resource conflicts
    public static class ConflictException extends ApplicationException {
        public ConflictException(String message) {
            super(message);
        }
        public ConflictException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // authentication and authorization errors
    public static class SecurityException extends ApplicationException {
        public SecurityException(String message) {
            super(message);
        }

        public SecurityException(String message, Throwable cause) {
            super(message, cause);
        }

        public static class AuthenticationException extends SecurityException {
            public AuthenticationException(String message) {
                super(message);
            }
        }

        public static class TokenException extends SecurityException {
            public TokenException(String message) {
                super(message);
            }
        }
    }

    // business rule violations
    public static class BusinessRuleException extends ApplicationException {
        public BusinessRuleException(String message) {
            super(message);
        }
    }
}