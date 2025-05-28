package nl.hu.bep.presentation.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
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

@Provider
@Slf4j
public class ExceptionMappers implements ExceptionMapper<Throwable> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        String path = uriInfo != null ? uriInfo.getPath() : "";

        if (exception instanceof BaseException) {
            logBaseException((BaseException) exception, path);
        } else {
            log.error("Uncaught exception at {}: {}", path, exception.getMessage(), exception);
        }

        if (exception instanceof BaseException) {
            return ((BaseException) exception).toResponse(uriInfo);
        }

        return createStandardExceptionResponse(exception, path);
    }

    private Response createStandardExceptionResponse(Throwable exception, String path) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String message = exception.getMessage();
        Map<String, Object> details = new HashMap<>();

        // Handle common exception types
        if (exception instanceof NullPointerException || exception instanceof IllegalArgumentException) {
            status = Response.Status.BAD_REQUEST;
            message = exception.getMessage() != null ? exception.getMessage() : "Validation error";
        } else if (exception instanceof ConstraintViolationException) {
            status = Response.Status.BAD_REQUEST;
            message = "Validation constraint violation";
            details.put("constraint", exception.getMessage());
        } else if (exception instanceof EntityNotFoundException) {
            status = Response.Status.NOT_FOUND;
            message = exception.getMessage() != null ? exception.getMessage() : "Resource not found";
        } else if (exception instanceof EntityExistsException) {
            status = Response.Status.CONFLICT;
            message = exception.getMessage() != null ? exception.getMessage() : "Resource already exists";
        } else if (exception instanceof PersistenceException) {
            status = Response.Status.BAD_REQUEST;
            message = "Database operation failed";
            details.put("error", exception.getMessage());
        } else if (exception instanceof WebApplicationException) {
            status = Response.Status.fromStatusCode(
                    ((WebApplicationException) exception).getResponse().getStatus());
        }

        // Include exception details for better debugging
        details.put("exceptionType", exception.getClass().getSimpleName());
        if (exception.getCause() != null) {
            details.put("cause", exception.getCause().getClass().getSimpleName());
            details.put("causeMessage", exception.getCause().getMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.of(
                status,
                exception.getClass().getSimpleName(),
                message != null ? message : "An error occurred",
                path,
                details);

        return Response
                .status(status)
                .entity(ApiResponse.error(errorResponse, message != null ? message : "An error occurred"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void logBaseException(BaseException exception, String path) {
        Response.Status status = exception.getStatus();

        if (status.getFamily() == Response.Status.Family.SERVER_ERROR) {
            log.error("Exception at {}: {} (Status: {})", path, exception.getMessage(), status, exception);
        } else {
            log.warn("Exception at {}: {} (Status: {})", path, exception.getMessage(), status);
        }
    }
}