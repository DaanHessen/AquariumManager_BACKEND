package nl.hu.bep.data.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for better testability and abstraction.
 * Follows Repository pattern with clear separation of concerns.
 */
public interface IRepository<T, ID> {
    
    Optional<T> findById(ID id);
    List<T> findAll();
    T insert(T entity);
    T update(T entity);
    void deleteById(ID id);
    List<T> findByField(String fieldName, Object value);
}
