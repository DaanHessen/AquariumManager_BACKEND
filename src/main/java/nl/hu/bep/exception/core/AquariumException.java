package nl.hu.bep.exception.core;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.presentation.dto.ErrorResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Core base exception for the Aquarium Management System.
 * Provides enterprise-grade error handling with consistent response formatting,
 * error tracking, and proper HTTP status mapping.
 */
public abstract class AquariumException extends RuntimeException {
    
    private final Response.Status status;
    private final String errorCode;
    private final String errorId;
    private final LocalDateTime timestamp;
    private final Map<String, Object> details;

    protected AquariumException(String message, Response.Status status, String errorCode) {
        this(message, status, errorCode, null, null);
    }

    protected AquariumException(String message, Response.Status status, String errorCode, Throwable cause) {
        this(message, status, errorCode, cause, null);
    }

    protected AquariumException(String message, Response.Status status, String errorCode, 
                               Throwable cause, Map<String, Object> details) {
        super(message, cause);
        this.status = status;
        this.errorCode = errorCode;
        this.errorId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    // ========== GETTERS ==========
    
    public Response.Status getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorId() {
        return errorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    // ========== RESPONSE GENERATION ==========
    
    /**
     * Converts this exception to a JAX-RS Response with proper formatting.
     */
    public Response toResponse(UriInfo uriInfo) {
        String path = uriInfo != null ? uriInfo.getPath() : "";
        
        Map<String, Object> errorDetails = new java.util.HashMap<>();
        if (details != null) {
            errorDetails.putAll(details);
        }
        errorDetails.put("errorCode", errorCode);
        errorDetails.put("errorId", errorId);
        errorDetails.put("timestamp", timestamp);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                status,
                errorCode,
                getMessage(),
                path,
                errorDetails
        );

        return Response
                .status(status)
                .entity(ApiResponse.error(errorResponse, getMessage()))
                .build();
    }

    // ========== LOGGING SUPPORT ==========
    
    /**
     * Returns a formatted string for logging purposes.
     */
    public String getLoggingInfo() {
        return String.format("[%s] %s - %s (ID: %s)", 
                errorCode, status, getMessage(), errorId);
    }

    /**
     * Determines if this exception should be logged as an error (vs warning).
     */
    public boolean isServerError() {
        return status.getFamily() == Response.Status.Family.SERVER_ERROR;
    }
} 