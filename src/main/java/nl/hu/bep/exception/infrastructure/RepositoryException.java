package nl.hu.bep.exception.infrastructure;

import jakarta.ws.rs.core.Response;
import nl.hu.bep.exception.core.AquariumException;

import java.util.Map;

/**
 * Infrastructure layer exceptions for data access and persistence operations.
 * Handles database connection issues, query failures, and data integrity violations.
 */
public class RepositoryException extends AquariumException {

    public RepositoryException(String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR, "REPOSITORY_ERROR");
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR, "REPOSITORY_ERROR", cause);
    }

    public RepositoryException(String message, Throwable cause, Map<String, Object> details) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR, "REPOSITORY_ERROR", cause, details);
    }

    // ========== SPECIFIC REPOSITORY EXCEPTIONS ==========

    /**
     * Database connection and transaction failures.
     */
    public static class DatabaseException extends RepositoryException {
        public DatabaseException(String message, Throwable cause) {
            super("Database operation failed: " + message, cause,
                  Map.of("operation", "database_access", "errorType", "connection_failure"));
        }
    }

    /**
     * Entity not found in repository operations.
     */
    public static class EntityNotFoundException extends RepositoryException {
        public EntityNotFoundException(String entityType, Long id) {
            super(String.format("Entity %s with ID %d not found", entityType, id), null,
                  Map.of("entityType", entityType, "entityId", id));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.NOT_FOUND;
        }
    }

    /**
     * Data integrity constraint violations.
     */
    public static class DataIntegrityException extends RepositoryException {
        public DataIntegrityException(String message, Throwable cause) {
            super("Data integrity violation: " + message, cause,
                  Map.of("errorType", "constraint_violation"));
        }

        @Override
        public Response.Status getStatus() {
            return Response.Status.CONFLICT;
        }
    }

    /**
     * Query execution and mapping failures.
     */
    public static class QueryException extends RepositoryException {
        public QueryException(String operation, Throwable cause) {
            super(String.format("Query failed for operation: %s", operation), cause,
                  Map.of("operation", operation, "errorType", "query_failure"));
        }
    }
} 