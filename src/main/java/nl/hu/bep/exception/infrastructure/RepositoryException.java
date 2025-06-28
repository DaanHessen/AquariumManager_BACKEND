package nl.hu.bep.exception.infrastructure;

import nl.hu.bep.exception.ApplicationException;

@Deprecated
public class RepositoryException extends ApplicationException {
    
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}