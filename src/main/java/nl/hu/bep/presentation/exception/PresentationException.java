package nl.hu.bep.presentation.exception;

import jakarta.ws.rs.core.Response;

public class PresentationException extends BaseException {

  public PresentationException(String message, Response.Status status) {
    super(message, status);
  }

  public PresentationException(String message, Throwable cause, Response.Status status) {
    super(message, cause, status);
  }

  public static class NullValueException extends PresentationException {
    public NullValueException(String message) {
      super(message != null ? message : "A required field is missing or null", Response.Status.BAD_REQUEST);
    }

    public NullValueException(String fieldName, Throwable cause) {
      super("Null value not allowed for field: " + fieldName, cause, Response.Status.BAD_REQUEST);
    }
  }

  public static class ValidationException extends PresentationException {
    public ValidationException(String message) {
      super(message, Response.Status.BAD_REQUEST);
    }

    public ValidationException(String message, Throwable cause) {
      super(message, cause, Response.Status.BAD_REQUEST);
    }
  }

  public static class UnexpectedException extends PresentationException {
    public UnexpectedException(String message) {
      super(message, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public UnexpectedException(String message, Throwable cause) {
      super(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
    }
  }

  public static class WebException extends PresentationException {
    public WebException(String message, Response.Status status) {
      super(message, status);
    }

    public WebException(String message, Throwable cause, Response.Status status) {
      super(message, cause, status);
    }
  }
}