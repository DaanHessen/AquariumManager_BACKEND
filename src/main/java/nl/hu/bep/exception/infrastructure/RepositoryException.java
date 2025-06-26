package nl.hu.bep.exception.infrastructure;

import nl.hu.bep.exception.ApplicationException;

/**
 * Infrastructure layer exception for repository and data access errors.
 * Simplified to extend ApplicationException for consistency.
 * 
 * @deprecated This class is obsolete and will be removed in future versions.
 *             Use ApplicationException or its static inner classes for repository errors.
 */
@Deprecated
public class RepositoryException extends ApplicationException {
    
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}