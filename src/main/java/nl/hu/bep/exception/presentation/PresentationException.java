package nl.hu.bep.exception.presentation;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.exception.core.AquariumException;

import java.util.Map;

/**
 * Presentation layer exceptions for API request/response processing,
 * validation, and HTTP-specific error handling.
 */
public class PresentationException extends AquariumException {

    public PresentationException(String message) {
        super(message, Response.Status.BAD_REQUEST, "PRESENTATION_ERROR");
    }

    public PresentationException(String message, Response.Status status, String errorCode) {
        super(message, status, errorCode);
    }

    public PresentationException(String message, Response.Status status, String errorCode, Map<String, Object> details) {
        super(message, status, errorCode, null, details);
    }

    // ========== SPECIFIC PRESENTATION EXCEPTIONS ==========

    /**
     * Request validation failures.
     */
    public static class ValidationException extends PresentationException {
        public ValidationException(String message) {
            super(message, Response.Status.BAD_REQUEST, "VALIDATION_ERROR");
        }

        public ValidationException(String field, String value, String constraint) {
            super(String.format("Validation failed for field '%s': %s", field, constraint),
                  Response.Status.BAD_REQUEST, "VALIDATION_ERROR",
                  Map.of("field", field, "value", value, "constraint", constraint));
        }
    }

    /**
     * JSON/XML mapping and serialization errors.
     */
    public static class MappingException extends PresentationException {
        public MappingException(String message, Throwable cause) {
            super("Request/response mapping failed: " + message, Response.Status.BAD_REQUEST, "MAPPING_ERROR",
                  Map.of("errorType", "serialization_failure"));
        }
    }

    /**
     * Malformed or invalid request format.
     */
    public static class BadRequestException extends PresentationException {
        public BadRequestException(String message) {
            super(message, Response.Status.BAD_REQUEST, "BAD_REQUEST");
        }

        public BadRequestException(String parameter, String expectedFormat) {
            super(String.format("Invalid request parameter '%s', expected format: %s", parameter, expectedFormat),
                  Response.Status.BAD_REQUEST, "BAD_REQUEST",
                  Map.of("parameter", parameter, "expectedFormat", expectedFormat));
        }
    }

    /**
     * Resource not found in API endpoints.
     */
    public static class ResourceNotFoundException extends PresentationException {
        public ResourceNotFoundException(String resourceType, String identifier) {
            super(String.format("%s not found: %s", resourceType, identifier),
                  Response.Status.NOT_FOUND, "RESOURCE_NOT_FOUND",
                  Map.of("resourceType", resourceType, "identifier", identifier));
        }
    }
} 