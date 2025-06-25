package nl.hu.bep.data.interfaces;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    
    Optional<T> findById(ID id);
    List<T> findAll();
    T insert(T entity);
    T update(T entity);
    void deleteById(ID id);
    List<T> findByField(String fieldName, Object value);
}
