package nl.hu.bep.presentation.dto.response;

import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    long timestamp,
    Map<String, Object> details) {

  public static ErrorResponse of(Response.Status status, String error, String message, String path) {
    return new ErrorResponse(
        status.getStatusCode(),
        error,
        message,
        path,
        Instant.now().toEpochMilli(),
        Collections.emptyMap());
  }

  public static ErrorResponse of(Response.Status status, String error, String message, String path,
      Map<String, Object> details) {
    return new ErrorResponse(
        status.getStatusCode(),
        error,
        message,
        path,
        Instant.now().toEpochMilli(),
        details);
  }

  public static ErrorResponse of(Response.Status status, Throwable throwable, String path) {
    Map<String, Object> details = new HashMap<>();
    details.put("exceptionType", throwable.getClass().getSimpleName());

    if (throwable.getCause() != null) {
      details.put("cause", throwable.getCause().getClass().getSimpleName());
    }

    return new ErrorResponse(
        status.getStatusCode(),
        throwable.getClass().getSimpleName(),
        throwable.getMessage() != null ? throwable.getMessage() : "An error occurred",
        path,
        Instant.now().toEpochMilli(),
        details);
  }
}