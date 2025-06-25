package nl.hu.bep.exception;

import nl.hu.bep.config.AquariumConstants;

/**
 * Unified exception hierarchy for the Aquarium Management System.
 * Modern approach: runtime exceptions only, handled by exception mappers.
 * Follows current Jakarta EE best practices.
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    // ========== SPECIFIC APPLICATION EXCEPTIONS ==========

    public static class NotFoundException extends ApplicationException {
        public NotFoundException(String message) {
            super(message);
        }

        public NotFoundException(String resourceType, Long id) {
            super(resourceType + " with ID " + id + " not found");
        }
    }

    public static class ValidationException extends ApplicationException {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
        
        public static ValidationException invalidLength(String field, int actual, int min, int max) {
            return new ValidationException(
                String.format("%s length must be between %d and %d characters, but was %d", 
                    field, min, max, actual));
        }
        
        public static ValidationException invalidRange(String field, double actual, double min, double max) {
            return new ValidationException(
                String.format("%s must be between %.2f and %.2f, but was %.2f", 
                    field, min, max, actual));
        }
    }

    public static class ConflictException extends ApplicationException {
        public ConflictException(String message) {
            super(message);
        }

        public ConflictException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UnauthorizedException extends ApplicationException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    public static class BadRequestException extends ApplicationException {
        public BadRequestException(String message) {
            super(message);
        }

        public BadRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ========== DOMAIN-SPECIFIC EXCEPTIONS ==========

    public static class IncompatibleWaterTypeException extends ValidationException {
        public IncompatibleWaterTypeException(String aquariumWaterType, String entityWaterType) {
            super(String.format("Water type incompatibility: Aquarium requires %s but entity requires %s", 
                    aquariumWaterType, entityWaterType));
        }
    }

    public static class OwnershipViolationException extends UnauthorizedException {
        public OwnershipViolationException(String entityType, Long entityId) {
            super(String.format("You don't have permission to access %s with ID %d", entityType, entityId));
        }
    }

    public static class EntityAlreadyAssignedException extends ConflictException {
        public EntityAlreadyAssignedException(String entityType, Long entityId, Long aquariumId) {
            super(String.format("%s %d is already assigned to aquarium %d", entityType, entityId, aquariumId));
        }
    }
    
    // ========== SECURITY EXCEPTIONS ==========
    
    public static class SecurityException extends ApplicationException {
        public SecurityException(String message) {
            super(message);
        }
        
        public SecurityException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class AuthenticationException extends SecurityException {
        public AuthenticationException(String message) {
            super(message);
        }
    }
    
    public static class AuthorizationException extends SecurityException {
        public AuthorizationException(String message) {
            super(message);
        }
    }
}