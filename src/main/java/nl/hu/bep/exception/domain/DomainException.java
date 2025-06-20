package nl.hu.bep.exception.domain;

import jakarta.ws.rs.core.Response;

import java.util.Map;

/**
 * Domain layer exceptions for business logic violations and entity validation errors.
 * Extends BusinessRuleException to provide specific domain error handling.
 * 
 * This class serves as the primary exception type for domain layer operations,
 * ensuring consistent error handling across all domain entities and services.
 */
public class DomainException extends BusinessRuleException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(String message, Map<String, Object> details) {
        super(message, details);
    }

    // ========== SPECIALIZED DOMAIN EXCEPTIONS ==========

    /**
     * Validation errors specific to domain entities and value objects.
     */
    public static class ValidationException extends DomainException {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String field, String constraint) {
            super(String.format("Validation failed for field '%s': %s", field, constraint),
                  Map.of("field", field, "constraint", constraint));
        }

        public ValidationException(String message, Map<String, Object> details) {
            super(message, details);
        }
    }

    /**
     * Incompatible water type between aquarium and inhabitants/equipment.
     */
    public static class IncompatibleWaterTypeException extends DomainException {
        public IncompatibleWaterTypeException(String aquariumWaterType, String entityWaterType) {
            super(String.format("Water type incompatibility: Aquarium requires %s but entity requires %s", 
                    aquariumWaterType, entityWaterType),
                  Map.of("aquariumWaterType", aquariumWaterType, "entityWaterType", entityWaterType));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CONFLICT;
        }
    }

    /**
     * Invalid state transitions for aquarium lifecycle management.
     */
    public static class InvalidStateTransitionException extends DomainException {
        public InvalidStateTransitionException(String fromState, String toState) {
            super(String.format("Invalid state transition from %s to %s", fromState, toState),
                  Map.of("fromState", fromState, "toState", toState));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CONFLICT;
        }
    }

    /**
     * Entity capacity or limit violations.
     */
    public static class CapacityExceededException extends DomainException {
        public CapacityExceededException(String entityType, int currentCount, int maxCapacity) {
            super(String.format("Capacity exceeded for %s: current %d, maximum %d", 
                    entityType, currentCount, maxCapacity),
                  Map.of("entityType", entityType, "currentCount", currentCount, "maxCapacity", maxCapacity));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CONFLICT;
        }
    }
} 