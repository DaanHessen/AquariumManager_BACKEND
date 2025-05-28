package nl.hu.bep.application.exception;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.presentation.exception.BaseException;

public class ApplicationException extends BaseException {
    public ApplicationException(String message, Response.Status status) {
        super(message, status);
    }

    public ApplicationException(String message, Throwable cause, Response.Status status) {
        super(message, cause, status);
    }

    public static class NotFoundException extends ApplicationException {
        public NotFoundException(String message) {
            super(message, Response.Status.NOT_FOUND);
        }

        public NotFoundException(String resourceType, Long id) {
            super(resourceType + " with ID " + id + " not found", Response.Status.NOT_FOUND);
        }
    }

    public static class BadRequestException extends ApplicationException {
        public BadRequestException(String message) {
            super(message, Response.Status.BAD_REQUEST);
        }

        public BadRequestException(String message, Throwable cause) {
            super(message, cause, Response.Status.BAD_REQUEST);
        }
    }

    public static class ValidationException extends ApplicationException {
        public ValidationException(String message) {
            super(message, Response.Status.BAD_REQUEST);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause, Response.Status.BAD_REQUEST);
        }
    }

    public static class ConflictException extends ApplicationException {
        public ConflictException(String message) {
            super(message, Response.Status.CONFLICT);
        }

        public ConflictException(String message, Throwable cause) {
            super(message, cause, Response.Status.CONFLICT);
        }
    }

    public static class IncompatibleWaterTypeException extends ApplicationException {
        public IncompatibleWaterTypeException(String message) {
            super(message, Response.Status.BAD_REQUEST);
        }

        public IncompatibleWaterTypeException(String message, Throwable cause) {
            super(message, cause, Response.Status.BAD_REQUEST);
        }
    }
}