package nl.hu.bep.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.presentation.dto.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception mapper following modern Jakarta REST best practices.
 * Maps all application exceptions to proper HTTP responses with meaningful error messages.
 * 
 * Updated to handle the modern AquariumException hierarchy with enhanced error tracking.
 */
@Provider
@Slf4j
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        String path = uriInfo != null ? uriInfo.getPath() : "";
        
        // Handle modern AquariumException hierarchy first
        if (exception instanceof nl.hu.bep.exception.core.AquariumException) {
            return handleAquariumException((nl.hu.bep.exception.core.AquariumException) exception);
        }
        
        // Log the exception appropriately
        if (isClientError(exception)) {
            log.warn("Client error at {}: {}", path, exception.getMessage());
        } else {
            log.error("Server error at {}: {}", path, exception.getMessage(), exception);
        }

        // Handle Jakarta validation exceptions
        if (exception instanceof ConstraintViolationException) {
            return handleValidationException((ConstraintViolationException) exception, path);
        }

        // Legacy ApplicationException support (to be deprecated)
        if (exception instanceof ApplicationException.NotFoundException) {
            return createErrorResponse(Response.Status.NOT_FOUND, exception, path);
        }
        
        if (exception instanceof ApplicationException.ValidationException) {
            return createErrorResponse(Response.Status.BAD_REQUEST, exception, path);
        }
        
        if (exception instanceof ApplicationException.ConflictException) {
            return createErrorResponse(Response.Status.CONFLICT, exception, path);
        }
        
        if (exception instanceof ApplicationException.UnauthorizedException) {
            return createErrorResponse(Response.Status.FORBIDDEN, exception, path);
        }
        
        if (exception instanceof ApplicationException.BadRequestException) {
            return createErrorResponse(Response.Status.BAD_REQUEST, exception, path);
        }

        // Handle generic application exceptions
        if (exception instanceof ApplicationException) {
            return createErrorResponse(Response.Status.BAD_REQUEST, exception, path);
        }

        // Handle unexpected exceptions
        return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, exception, path);
    }

    /**
     * Handle modern AquariumException hierarchy with enhanced error tracking.
     */
    private Response handleAquariumException(nl.hu.bep.exception.core.AquariumException exception) {
        // Log appropriately based on error severity
        if (exception.isServerError()) {
            log.error("Server error [{}]: {} (ID: {})", 
                     exception.getErrorCode(), exception.getMessage(), exception.getErrorId(), exception);
        } else {
            log.warn("Client error [{}]: {} (ID: {})", 
                     exception.getErrorCode(), exception.getMessage(), exception.getErrorId());
        }

        // Use the exception's built-in response generation
        return exception.toResponse(uriInfo);
    }

    private Response createErrorResponse(Response.Status status, Throwable exception, String path) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            message = "An error occurred";
        }

        ErrorResponse errorResponse = ErrorResponse.of(status, 
                exception.getClass().getSimpleName(), 
                message, 
                path);

        return Response
                .status(status)
                .entity(ApiResponse.error(errorResponse, message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleValidationException(ConstraintViolationException exception, String path) {
        Map<String, Object> validationErrors = new HashMap<>();
        
        exception.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            // Extract just the field name (remove method prefix if present)
            if (fieldName.contains(".")) {
                fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
            }
            validationErrors.put(fieldName, violation.getMessage());
        });

        ErrorResponse errorResponse = ErrorResponse.of(
                Response.Status.BAD_REQUEST,
                "ValidationError", 
                "Validation failed for one or more fields", 
                path,
                Map.of("validationErrors", validationErrors)
        );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.error(errorResponse, "Validation failed"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private boolean isClientError(Throwable exception) {
        return exception instanceof ApplicationException.NotFoundException ||
               exception instanceof ApplicationException.ValidationException ||
               exception instanceof ApplicationException.ConflictException ||
               exception instanceof ApplicationException.UnauthorizedException ||
               exception instanceof ApplicationException.BadRequestException ||
               exception instanceof ConstraintViolationException;
    }
} 