package nl.hu.bep.exception;

import nl.hu.bep.presentation.dto.response.ApiResponse;
import nl.hu.bep.presentation.dto.response.ErrorResponse;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Provider
@Slf4j
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        String path = uriInfo != null ? uriInfo.getPath() : "";

        if (exception instanceof ApplicationException.NotFoundException) {
            return createErrorResponse(Response.Status.NOT_FOUND, exception, path);
        }
        if (exception instanceof ApplicationException.ValidationException) {
            return createErrorResponse(Response.Status.BAD_REQUEST, exception, path);
        }
        if (exception instanceof ApplicationException.ConflictException) {
            return createErrorResponse(Response.Status.CONFLICT, exception, path);
        }
        if (exception instanceof ApplicationException.SecurityException.AuthenticationException) {
            return createErrorResponse(Response.Status.FORBIDDEN, exception, path);
        }
        if (exception instanceof ApplicationException.SecurityException.TokenException) {
            return createErrorResponse(Response.Status.UNAUTHORIZED, exception, path);
        }
        if (exception instanceof ApplicationException.SecurityException) {
            return createErrorResponse(Response.Status.FORBIDDEN, exception, path);
        }
        if (exception instanceof ApplicationException.BusinessRuleException) {
            return createErrorResponse(Response.Status.BAD_REQUEST, exception, path);
        }
        if (exception instanceof ConstraintViolationException) {
            return handleValidationException((ConstraintViolationException) exception, path);
        }
        if (exception instanceof ApplicationException) {
            return createErrorResponse(Response.Status.BAD_REQUEST, exception, path);
        }
        return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, exception, path);
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
}