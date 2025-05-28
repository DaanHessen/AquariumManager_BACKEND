package nl.hu.bep.domain.exception;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.presentation.exception.BaseException;

public class DomainException extends BaseException {
  public DomainException(String message) {
    super(message, Response.Status.BAD_REQUEST);
  }

  public DomainException(String message, Throwable cause) {
    super(message, cause, Response.Status.BAD_REQUEST);
  }

  public static class IncompatibleWaterTypeException extends DomainException {
    public IncompatibleWaterTypeException(String message) {
      super(message);
    }

    public IncompatibleWaterTypeException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class ValidationException extends DomainException {
    public ValidationException(String message) {
      super(message);
    }

    public ValidationException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class NotFoundException extends DomainException {
    public NotFoundException(String message) {
      super(message);
    }

    public NotFoundException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
