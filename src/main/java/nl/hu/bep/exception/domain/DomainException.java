package nl.hu.bep.exception.domain;

import jakarta.ws.rs.core.Response;

import java.util.Map;

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