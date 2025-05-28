package nl.hu.bep.data.exception;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.presentation.exception.BaseException;

public class RepositoryException extends BaseException {
  public RepositoryException(String message, Throwable cause) {
    super(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
  }

  public RepositoryException(String message) {
    super(message, Response.Status.INTERNAL_SERVER_ERROR);
  }

  public static class DatabaseConfigException extends RepositoryException {
    public DatabaseConfigException(String message) {
      super(message);
    }

    public DatabaseConfigException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class ConnectionException extends RepositoryException {
    public ConnectionException(String message) {
      super(message);
    }

    public ConnectionException(String message, Throwable cause) {
      super(message, cause);
    }

    @Override
    public Response.Status getStatus() {
      return Response.Status.SERVICE_UNAVAILABLE;
    }
  }

  public static class ConfigurationException extends RepositoryException {
    public ConfigurationException(String message) {
      super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class EntityNotFoundException extends RepositoryException {
    public EntityNotFoundException(String message) {
      super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }

    @Override
    public Response.Status getStatus() {
      return Response.Status.NOT_FOUND;
    }
  }

  public static class ConstraintViolationException extends RepositoryException {
    public ConstraintViolationException(String message) {
      super(message);
    }

    public ConstraintViolationException(String message, Throwable cause) {
      super(message, cause);
    }

    @Override
    public Response.Status getStatus() {
      return Response.Status.CONFLICT;
    }
  }
}
