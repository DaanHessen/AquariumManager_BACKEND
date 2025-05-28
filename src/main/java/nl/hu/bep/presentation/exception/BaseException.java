package nl.hu.bep.presentation.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import nl.hu.bep.presentation.dto.ApiResponse;
import nl.hu.bep.presentation.dto.ErrorResponse;

public abstract class BaseException extends RuntimeException {
  private final Response.Status status;

  protected BaseException(String message, Response.Status status) {
    super(message);
    this.status = status;
  }

  protected BaseException(String message, Throwable cause, Response.Status status) {
    super(message, cause);
    this.status = status;
  }

  public Response.Status getStatus() {
    return status;
  }

  public Response toResponse(UriInfo uriInfo) {
    String path = uriInfo != null ? uriInfo.getPath() : "";
    ErrorResponse errorResponse = ErrorResponse.of(status, this, path);

    return Response
        .status(status)
        .entity(ApiResponse.error(errorResponse, getMessage()))
        .build();
  }
}