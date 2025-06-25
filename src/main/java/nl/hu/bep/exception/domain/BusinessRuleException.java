package nl.hu.bep.exception.domain;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.exception.core.AquariumException;

import java.util.Map;

public class BusinessRuleException extends AquariumException {

    public BusinessRuleException(String message) {
        super(message, Response.Status.BAD_REQUEST, "BUSINESS_RULE_VIOLATION");
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, Response.Status.BAD_REQUEST, "BUSINESS_RULE_VIOLATION", cause);
    }

    public BusinessRuleException(String message, Map<String, Object> details) {
        super(message, Response.Status.BAD_REQUEST, "BUSINESS_RULE_VIOLATION", null, details);
    }

    public static class IncompatibleWaterTypeException extends BusinessRuleException {
        public IncompatibleWaterTypeException(String aquariumWaterType, String entityWaterType) {
            super(String.format("Water type incompatibility: Aquarium requires %s but entity requires %s", 
                    aquariumWaterType, entityWaterType),
                  Map.of("aquariumWaterType", aquariumWaterType, "entityWaterType", entityWaterType));
        }
    }

    public static class CapacityExceededException extends BusinessRuleException {
        public CapacityExceededException(String entityType, int currentCount, int maxCapacity) {
            super(String.format("Capacity exceeded for %s: current %d, maximum %d", 
                    entityType, currentCount, maxCapacity),
                  Map.of("entityType", entityType, "currentCount", currentCount, "maxCapacity", maxCapacity));
        }
    }

    public static class InvalidStateTransitionException extends BusinessRuleException {
        public InvalidStateTransitionException(String fromState, String toState) {
            super(String.format("Invalid state transition from %s to %s", fromState, toState),
                  Map.of("fromState", fromState, "toState", toState));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CONFLICT;
        }
    }

    public static class DomainValidationException extends BusinessRuleException {
        public DomainValidationException(String field, String value, String constraint) {
            super(String.format("Domain validation failed for field '%s': %s", field, constraint),
                  Map.of("field", field, "value", value, "constraint", constraint));
        }
    }

    public static class OwnershipViolationException extends BusinessRuleException {
        public OwnershipViolationException(String entityType, Long entityId, Long ownerId) {
            super(String.format("Ownership violation: %s %d does not belong to owner %d", 
                    entityType, entityId, ownerId),
                  Map.of("entityType", entityType, "entityId", entityId, "ownerId", ownerId));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.FORBIDDEN;
        }
    }

    public static class EntityAlreadyAssignedException extends BusinessRuleException {
        public EntityAlreadyAssignedException(String entityType, Long entityId, Long currentAquariumId) {
            super(String.format("%s %d is already assigned to aquarium %d", 
                    entityType, entityId, currentAquariumId),
                  Map.of("entityType", entityType, "entityId", entityId, "currentAquariumId", currentAquariumId));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CONFLICT;
        }
    }
} 