package nl.hu.bep.exception.core;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.presentation.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
    
    public String getLoggingInfo() {
        return String.format("[%s] %s - %s (ID: %s)", 
                errorCode, status, getMessage(), errorId);
    }

    public boolean isServerError() {
        return status.getFamily() == Response.Status.Family.SERVER_ERROR;
    }
} 